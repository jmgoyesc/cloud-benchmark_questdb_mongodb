package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.ports.DatabasePort;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Juan Manuel Goyes Coral
 */

@Log4j2
@Getter
@RequiredArgsConstructor
public final class Actor implements Runnable {

    @Setter private boolean running = false;
    private final AtomicLong success = new AtomicLong(0L);
    private final AtomicLong fail = new AtomicLong(0L);
    private final AtomicLong total = new AtomicLong(0L);
    private final int index;
    private final DatabasePort database;

    @Override
    public void run() {
        log.info("Thread {} started", index);
        while (running) {

            try {
                var telemetry = TelemetryGenerator.build(index);
                database.insert(telemetry);
                success.incrementAndGet();
            } catch (Exception e) {
                fail.incrementAndGet();
            } finally {
                total.incrementAndGet();
            }
        }
        log.info("Thread {} finished. Stats (success={}, fail={}, total={})", index, success.get(), fail.get(), total.get());
    }

    public int index() {
        return index;
    }

    public DatabasePort database() {
        return database;
    }

    public void resetCounters() {
        success.lazySet(0);
        fail.lazySet(0);
        total.lazySet(0);
    }

}
