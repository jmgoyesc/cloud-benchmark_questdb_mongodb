package com.github.jmgoyesc.agent.domain.models.config;

import com.github.jmgoyesc.agent.domain.models.ports.DatabasePort;
import com.github.jmgoyesc.agent.domain.services.Actor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Juan Manuel Goyes Coral
 */

@RequiredArgsConstructor
public final class Instance {
    private final List<Actor> actors;
    @Getter private final DatabasePort database;
    private List<Thread> threads = List.of();

    public void startAll() {
        initThreads();
        threads.forEach(Thread::start);
    }

    public ActorsStats stopAll() {
        setRunningFalseForAll();

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return collectStats();
    }

    private void initThreads( ) {
        this.threads = actors.stream()
                .peek(Actor::resetCounters)
                .peek(a -> a.setRunning(true))
                .map(runnable -> Thread.ofVirtual()
                        .name("virtual-gen-", runnable.index())
                        .unstarted(runnable))
                .toList();
    }

    private void setRunningFalseForAll() {
        for (Actor actor : actors) {
            actor.setRunning(false);
        }
    }

    private ActorsStats collectStats() {
        long success = actors.stream().mapToLong(a -> a.getSuccess().get()).sum();
        long fail = actors.stream().mapToLong(a -> a.getFail().get()).sum();
        long total = actors.stream().mapToLong(a -> a.getTotal().get()).sum();

        return new ActorsStats(success, fail, total, 0.0);
    }

}
