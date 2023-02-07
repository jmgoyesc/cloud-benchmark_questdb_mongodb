package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.Config;
import com.github.jmgoyesc.agent.domain.services.ports.DatabasePort;
import com.github.jmgoyesc.agent.domain.services.ports.MongodbPort;
import com.github.jmgoyesc.agent.domain.services.ports.QuestdbInfluxPort;
import com.github.jmgoyesc.agent.domain.services.ports.QuestdbPgPort;
import com.github.jmgoyesc.agent.domain.services.ports.QuestdbRestPort;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@RequiredArgsConstructor
class DatabasePortCreator {

    private final ApplicationContext context;
    private final RestTemplateBuilder builder;

    DatabasePort newInstance(Config config) {
        return switch (config.db()) {
            case mongodb -> context.getBean(MongodbPort.class, config.uri());
            case questdb_pg -> context.getBean(QuestdbPgPort.class, config.uri());
            case questdb_rest -> context.getBean(QuestdbRestPort.class, config.uri(), builder);
            case questdb_influx -> context.getBean(QuestdbInfluxPort.class, config.uri());
        };
    }
}
