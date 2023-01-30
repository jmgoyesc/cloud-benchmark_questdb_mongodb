package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.Config;
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
        var port = switch (config.db()) {
            case mongodb -> mongodbPort;
            case questdb_pg -> questdbPgPort;
            case questdb_rest -> questdbRest;
            case questdb_influx -> questdbInfluxPort;
        };
        load = new LoadGenerator(port, config.uri(), true);
        tLoad = new Thread(load);
        tLoad.start();
        log.info("[start] Signal processed");
    }

    public void stop() {
        log.info("[stop] Signal received to stop process");
        load.setRunning(false);
        tLoad.interrupt();
        log.info("[stop] Signal processed");
    }
}
