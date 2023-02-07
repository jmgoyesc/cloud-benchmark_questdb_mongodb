package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.Config.Database;
import com.github.jmgoyesc.agent.domain.models.Stats;
import com.github.jmgoyesc.agent.domain.models.Telemetry;
import com.github.jmgoyesc.agent.domain.services.ports.DatabasePort;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Random;

/**
 * @author Juan Manuel Goyes Coral
 */


@Slf4j
@RequiredArgsConstructor
class LoadGenerator implements Runnable {

    static volatile boolean running = false;

    private final DatabasePort port;
    private final Database source;
    @Getter private final String vehicle;

    private long telemetries = 0L;
    private long error = 0L;

    @Override
    public void run() {
        while(running) {
            var telemetry = build(source);
            var done = port.insert(telemetry);
            telemetries++;
            if (!done) {
                error++;
            }
        }
        port.clean();
    }

    Stats stats() {
        return new Stats(telemetries, error);
    }

    private Telemetry build(Database source) {
        var now = ZonedDateTime.now();
        return Telemetry.builder()
                .receivedAt(now)
                .originatedAt(now)
                .vehicle(this.vehicle)
                .type(Type.giveOne().name())
                .source(source)
                .value(Math.random())
                .build();
    }

    enum Type {
        mileage, fuel_level, fuel_ratio, fuel_consumption;

        static Type giveOne() {
            return Type.values()[new Random().nextInt(Type.values().length)];
        }
    }
}
