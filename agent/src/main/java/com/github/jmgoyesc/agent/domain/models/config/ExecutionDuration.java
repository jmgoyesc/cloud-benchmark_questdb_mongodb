package com.github.jmgoyesc.agent.domain.models.config;

import java.time.Duration;

/**
 * @author Juan Manuel Goyes Coral
 */

public record ExecutionDuration(
        long nanos,
        long millis,
        long seconds
) {

    public static ExecutionDurationBuilder builder() {
        return new ExecutionDurationBuilder();
    }

    public static class ExecutionDurationBuilder {
        private long nanos;
        private long millis;
        private long seconds;

        public ExecutionDurationBuilder duration(Duration duration) {
            this.nanos = duration.toNanos();
            this.millis = duration.toMillis();
            this.seconds = duration.toSeconds();
            return this;
        }

        public ExecutionDuration build() {
            return new ExecutionDuration(nanos, millis, seconds);
        }

    }
}
