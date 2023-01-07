package com.github.jmgoyesc.agent.domain.models.config;

import java.time.ZonedDateTime;

/**
 * @author Juan Manuel Goyes Coral
 */

public record Execution(
        ZonedDateTime start,
        ZonedDateTime end,
        long vehicles,
        ExecutionDuration duration,
        double throughput,
        ActorsStats stats
) {


    public static ExecutionBuilder builder() {
        return new ExecutionBuilder();
    }

    public static class ExecutionBuilder {
        private ZonedDateTime start;
        private ZonedDateTime end;
        private long vehicles;
        private ExecutionDuration duration;
        private double throughput;
        private ActorsStats stats;

        public ExecutionBuilder start(ZonedDateTime start) {
            this.start = start;
            return this;
        }

        public ExecutionBuilder end(ZonedDateTime end) {
            this.end = end;
            return this;
        }

        public ExecutionBuilder vehicles(long vehicles) {
            this.vehicles = vehicles;
            return this;
        }

        public ExecutionBuilder duration(ExecutionDuration duration) {
            this.duration = duration;
            return this;
        }

        public ExecutionBuilder stats(ActorsStats stats) {
            this.stats = stats;
            return this;
        }

        public Execution build() {
            if (vehicles != 0L && duration != null && duration.seconds() != 0L) {
                throughput = (double) vehicles / (double) duration.seconds();
            }
            return new Execution(start, end, vehicles, duration, throughput, stats);
        }

    }
}
