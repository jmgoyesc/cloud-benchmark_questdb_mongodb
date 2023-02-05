package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.Config.Database;
import com.github.jmgoyesc.agent.domain.models.Telemetry;
import com.github.jmgoyesc.agent.domain.services.ports.DatabasePort;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Random;
import java.util.random.RandomGenerator;

/**
 * @author Juan Manuel Goyes Coral
 */


@Slf4j
class LoadGenerator implements Runnable {

    static volatile boolean running = false;

    private final DatabasePort port;
    private final Database source;
    private final String vehicle;

    LoadGenerator(DatabasePort port, Database source, String vehicle) {
        this.port = port;
        this.source = source;
        this.vehicle = vehicle;
    }

    @Override
    public void run() {
        log.info("Start load generator");
        while(running) {
            var telemetry = build(source);
            port.insert(telemetry);
        }
        log.info("End load generator");
    }

    private Telemetry build(Database source) {
        var now = ZonedDateTime.now();
        return Telemetry.builder()
                .receivedAt(now)
                .originatedAt(now)
                .vehicle(this.vehicle)
                .type("mileage")
                .source(source)
                .value(Random.from(RandomGenerator.getDefault()).nextDouble())
                .build();
    }
}
