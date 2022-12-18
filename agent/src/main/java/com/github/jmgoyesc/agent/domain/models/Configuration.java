package com.github.jmgoyesc.agent.domain.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * @author Juan Manuel Goyes Coral
 */

public record Configuration(
        String id,
        TargetDB target,
        int vehicles,
        Status status,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt,
        ConnectionProperties connection
) {

    public Configuration(TargetDB target, int vehicles, ConnectionProperties connectionProperties) {
        this(UUID.randomUUID().toString(), target, vehicles, Status.CREATED, ZonedDateTime.now(), ZonedDateTime.now(), connectionProperties);
    }

    public Configuration(Configuration source, Status status) {
        this(source.id, source.target, source.vehicles, status, source.createdAt, ZonedDateTime.now(), source.connection);
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

    public enum TargetDB {questdb_ilp, questdb_pg, quest_rest, mongo}
    public enum Status { CREATED, RUNNING, WAITING_FOR_TERMINATION, TERMINATED }

}
