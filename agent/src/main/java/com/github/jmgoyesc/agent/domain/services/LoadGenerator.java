package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.Config.Database;
import com.github.jmgoyesc.agent.domain.models.Telemetry;
import com.github.jmgoyesc.agent.domain.services.ports.DatabasePort;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Random;
import java.util.random.RandomGenerator;

/**
 * @author Juan Manuel Goyes Coral
 */


@Slf4j
class LoadGenerator implements Runnable {

    private final DatabasePort port;
    private final String uri;
    private final Database source;
    @Setter private boolean running;

    LoadGenerator(DatabasePort port, String uri, Database source, boolean running) {
        this.port = port;
        this.uri = uri;
        this.running = running;
        this.source = source;
    }

    @Override
    public void run() {
        log.info("Start load generator");
        while(running) {
            var telemetry = build(source);
            port.insert(uri, telemetry);
        }
        log.info("End load generator");
    }

    private static Telemetry build(Database source) {
        var now = ZonedDateTime.now();
        return Telemetry.builder()
                .receivedAt(now)
                .originatedAt(now)
                .vehicle("vehicle-1")
                .type("mileage")
                .source(source)
                .value(Random.from(RandomGenerator.getDefault()).nextDouble())
                .build();
    }
}
