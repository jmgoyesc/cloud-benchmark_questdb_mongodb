package com.github.jmgoyesc.agent.domain.models.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.time.ZonedDateTime.now;

/**
 * @author Juan Manuel Goyes Coral
 */

@Builder
@Jacksonized
@Getter
public final class Configuration {
    private final List<Execution> completed = new ArrayList<>();
    private final String id = UUID.randomUUID().toString();
    private final ZonedDateTime createdAt = now();

    private final TargetDB target;
    private final int vehicles;
    private final ConnectionProperties connection;

    @Builder.Default private Status status = Status.CREATED;
    private Execution running;

    public void start() {
        this.status = Status.RUNNING;
        this.running = Execution.builder().start(now()).build();
    }

    public void stop(int vehicles, ActorsStats stats) {
        var end = now();
        if (running == null || running.start() == null)
            throw new IllegalArgumentException("Missing execution start. Unable to execute end without previous start");

        this.status = Status.WAITING_FOR_TERMINATION;

        var duration = Duration.between(running.start(), end);

        var complete = Execution.builder()
                .start(running.start())
                .end(end)
                .vehicles(vehicles)
                .duration(ExecutionDuration.builder()
                        .duration(duration)
                        .build())
                .stats(stats.withThroughput((double) stats.getSuccess() / (double) duration.toSeconds()))
                .build();
        this.completed.add(complete);
        this.running = null;

        this.status = Status.TERMINATED;
    }

    @Validated
    public record ConnectionProperties(
            @NotBlank String url,
            @NotNull Map<String, String> values
    ) {

        public ConnectionProperties(String url) {
            this(url, Map.of());
        }

    }

    public enum Status {CREATED, RUNNING, WAITING_FOR_TERMINATION, TERMINATED}

}
