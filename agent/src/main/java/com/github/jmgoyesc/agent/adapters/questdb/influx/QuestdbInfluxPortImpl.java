package com.github.jmgoyesc.agent.adapters.questdb.influx;

import com.github.jmgoyesc.agent.domain.models.biz.Telemetry;
import com.github.jmgoyesc.agent.domain.models.config.Configuration;
import com.github.jmgoyesc.agent.domain.models.config.DatabaseStatus;
import com.github.jmgoyesc.agent.domain.models.ports.QuestdbInfluxLineProtocolPort;
import io.questdb.client.Sender;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Scope(SCOPE_PROTOTYPE)
@Log4j2
class QuestdbInfluxPortImpl implements QuestdbInfluxLineProtocolPort {
    @Override
    public void createdDS(Configuration configuration) {

    }

    @Override
    public void close() {

    }

    @Override
    public DatabaseStatus status() {
        return null;
    }

    @Override
    public void createTable() {

    }

    @Override
    public void dropTable() {

    }

    @Override
    public void cleanData() {

    }

    @Override
    public String insert(Telemetry<?> telemetry) {
        //TODO: complete the sender for ILP
        try (Sender sender = Sender.builder()
                .address("clever-black-363-c1213c97.ilp.b04c.questdb.net:32074")
                .enableTls()
                .enableAuth("admin").authToken("GwBXoGG5c6NoUTLXnzMxw_uNiVa8PKobzx5EiuylMW0")
                .build()) {
            sender.table("inventors")
                    .symbol("born", "Austrian Empire")
                    .longColumn("id", 0)
                    .stringColumn("name", "Nicola Tesla")
                    .atNow();
            sender.table("inventors")
                    .symbol("born", "USA")
                    .longColumn("id", 1)
                    .stringColumn("name", "Thomas Alva Edison")
                    .atNow();
        }
        return null;
    }

    @Override
    public int count() {
        return 0;
    }
}
