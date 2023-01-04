package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.biz.Telemetry;

import java.time.ZonedDateTime;
import java.util.Random;
import java.util.random.RandomGenerator;

import static java.time.ZonedDateTime.now;

/**
 * @author Juan Manuel Goyes Coral
 */

class TelemetryGenerator {

    static Telemetry<?> build(int index) {
        return Telemetry.<Double>builder()
                .vehicle("vehicle-%d".formatted(index))
                .type(Telemetry.Type.mileage)
                .originatedAt(now())
                .value(Random.from(RandomGenerator.getDefault()).nextDouble())
                .build();
    }

}
