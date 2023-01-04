package com.github.jmgoyesc.agent.domain.models;

import com.github.jmgoyesc.agent.domain.models.ports.DatabasePort;
import com.github.jmgoyesc.agent.domain.models.ports.MongodbPort;
import com.github.jmgoyesc.agent.domain.models.ports.QuestdbInfluxPort;
import com.github.jmgoyesc.agent.domain.models.ports.QuestdbPostgresPort;
import com.github.jmgoyesc.agent.domain.models.ports.QuestdbRestPort;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

import java.util.function.Function;

/**
 * @author Juan Manuel Goyes Coral
 */

@RequiredArgsConstructor
@Getter
public enum TargetDB {
    questdb_influx(context -> context.getBean(QuestdbInfluxPort.class)),
    questdb_postgres(context -> context.getBean(QuestdbPostgresPort.class)),
    quest_rest(context -> context.getBean(QuestdbRestPort.class)),
    mongo(context -> context.getBean(MongodbPort.class)),
    ;

    private final Function<ApplicationContext, DatabasePort> builder;

}
