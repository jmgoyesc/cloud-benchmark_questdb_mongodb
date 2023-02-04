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
    private LoadGenerator load;
    private Thread tLoad;

    public void configure(Config config) {
        this.config = config;
        log.info("[configure] Complete configuration => {}", config);
    }

    public void start() {
        log.info("[start] Signal received to start the process");
        if (config == null) {
            throw new RuntimeException("Configure was not called for agent. Pending configuration: POST /v1/configurations");
        }
        var db = chooseDatabase();
        load = new LoadGenerator(db, config.uri(), config.db(), true);
        tLoad = new Thread(load);
        tLoad.start();
        log.info("[start] Signal processed");
    }

    public void stop() {
        log.info("[stop] Signal received to stop process");
        if (load == null) {
            throw new RuntimeException("Not found a running process. Please configure and start it");
        }
        load.setRunning(false);
        log.info("[stop] Signal processed");
    }

    public long count() {
        if (config == null) {
            throw new RuntimeException("Configure was not called for agent. Pending configuration: POST /v1/configurations");
        }
        var db = chooseDatabase();
        return db.count(config.uri());
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
