package com.github.jmgoyesc.agent.domain.models.biz;

import lombok.Builder;

import java.time.ZonedDateTime;

/**
 * @author Juan Manuel Goyes Coral
 */

@Builder
public record Telemetry<T>(
        Type type,
        ZonedDateTime receivedAt,
        ZonedDateTime originatedAt,
        String vehicle,
        T value
) {

    public enum Type { mileage, fuel_level, fuel_ratio, fuel_consumption, position }

    public interface Value<T> {
        T value();
    }

    public record DoubleValue(Double value) implements Value<Double> { }
    public record LongValue(Long value) implements Value<Long> {}
    public record PositionValue(Position value) implements Value<Position> {}

}
