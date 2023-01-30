package com.github.jmgoyesc.agentquestdbrest.domain.services;

import com.github.jmgoyesc.agentquestdbrest.domain.models.Telemetry;
import com.github.jmgoyesc.agentquestdbrest.domain.services.ports.DatabasePort;
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
    @Setter private boolean running;

    LoadGenerator(DatabasePort port, String uri, boolean running) {
        this.port = port;
        this.uri = uri;
        this.running = running;
    }

    @Override
    public void run() {
        log.info("Start load generator");
        while(running) {
            var telemetry = build();
            port.insert(uri, telemetry);
        }
        log.info("End load generator");
    }

    private static Telemetry build() {
        var now = ZonedDateTime.now();
        return Telemetry.builder()
                .receivedAt(now)
                .originatedAt(now)
                .vehicle("vehicle-1")
                .type("mileage")
                .value(Random.from(RandomGenerator.getDefault()).nextDouble())
                .build();
    }
}
