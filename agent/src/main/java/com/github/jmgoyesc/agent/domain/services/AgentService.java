package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.Config;
import com.github.jmgoyesc.agent.domain.services.ports.DatabasePort;
import com.github.jmgoyesc.agent.domain.services.ports.MongodbPort;
import com.github.jmgoyesc.agent.domain.services.ports.QuestdbInfluxPort;
import com.github.jmgoyesc.agent.domain.services.ports.QuestdbPgPort;
import com.github.jmgoyesc.agent.domain.services.ports.QuestdbRestPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class AgentService {

    private final QuestdbRestPort questdbRest;
    private final QuestdbInfluxPort questdbInfluxPort;
    private final QuestdbPgPort questdbPgPort;
    private final MongodbPort mongodbPort;

    private Config config;
    private List<Thread> threads = List.of();

    public void configure(Config config) {
        this.config = config;
        var db = chooseDatabase();
        db.init(config.uri());

        this.threads = IntStream.rangeClosed(1, config.vehicles())
                        .mapToObj(it -> Thread.ofVirtual()
                                .name("vt-", it)
                                .unstarted(new LoadGenerator(db, config.db(), "vehicle-" + it)))
                        .toList();

        log.info("[configure] Complete configuration => {}", config);
    }

    public void start() {
        log.info("[start] Signal received to start the process");
        if (config == null) {
            throw new RuntimeException("Configure was not called for agent. Pending configuration: POST /v1/configurations");
        }
        LoadGenerator.running = true;
        threads.forEach(Thread::start);
        log.info("[start] Signal processed");
    }

    public void stop() {
        log.info("[stop] Signal received to stop process. Wait for all threads to finish: {}", threads.size());

        LoadGenerator.running = false;
        threads.parallelStream().forEach(thread -> {
            try {
                var terminated = thread.join(Duration.ofSeconds(5));
                if (!terminated) {
                    thread.interrupt();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        log.info("[stop] Signal processed");
    }

    public long count() {
        if (config == null) {
            throw new RuntimeException("Configure was not called for agent. Pending configuration: POST /v1/configurations");
        }
        var db = chooseDatabase();
        return db.count();
    }

    private DatabasePort chooseDatabase() {
        return switch (config.db()) {
            case mongodb -> mongodbPort;
            case questdb_pg -> questdbPgPort;
            case questdb_rest -> questdbRest;
            case questdb_influx -> questdbInfluxPort;
        };
    }
}
