package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.Stats;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Juan Manuel Goyes Coral
 */

@Slf4j
class MonitorService {

    private final Runnable task;

    private final List<LoadGenerator> workers;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final ScheduledFuture<?> monitor;
    private Stats previous = new Stats(0L, 0L);
    private long periodTelemetries, periodInserted, periodError, totalTelemetries, totalInserted, totalError;

    MonitorService(List<LoadGenerator> workers) {
        this.workers = workers;
        log.info("[stats] {},{},{},{},{},{},{}", pad("timestamp"), pad("period_telemetries"), pad("period_inserted"), pad("period_error"), pad("total_telemetries"), pad("total_inserted"), pad("total_error"));

        this.task = () -> {
            var current = this.workers.stream()
                    .map(LoadGenerator::stats)
                    .reduce(new Stats(0L, 0L), (a, b) -> new Stats(
                            a.telemetries() + b.telemetries(),
                            a.error() + b.error()
                    ));

            periodTelemetries = current.telemetries() - previous.telemetries();
            periodError = current.error() - previous.error();
            periodInserted = periodTelemetries - periodError;
            totalTelemetries = current.telemetries();
            totalError = current.error();
            totalInserted = totalTelemetries - totalError;
            previous = current;

            log.info("[stats] {},{},{},{},{},{},{}",
                    pad(System.currentTimeMillis()),
                    pad(periodTelemetries),
                    pad(periodInserted),
                    pad(periodError),
                    pad(totalTelemetries),
                    pad(totalInserted),
                    pad(totalError));
        };
        this.monitor = executor.scheduleAtFixedRate(task, 0L, 10L, TimeUnit.SECONDS);
    }

    Stats getResults() {
        return previous;
    }

    private static String pad(String text) {
        return StringUtils.leftPad(text, 18);
    }

    private static String pad(long value) {
        return StringUtils.leftPad(String.valueOf(value), 18);
    }

    void cancel() {
        this.monitor.cancel(false);
        this.executor.close();

        try (var executor = Executors.newSingleThreadExecutor()) {
            executor.submit(task);
        }
    }
}
