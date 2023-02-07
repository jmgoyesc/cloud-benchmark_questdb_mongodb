package com.github.jmgoyesc.agent.adapters.questdb;

import com.github.jmgoyesc.agent.domain.models.Telemetry;
import com.github.jmgoyesc.agent.domain.services.ports.QuestdbInfluxPort;
import io.questdb.client.Sender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Scope(SCOPE_PROTOTYPE)
@Slf4j
class InfluxPortImpl implements QuestdbInfluxPort {

    private final Sender sender;

    public InfluxPortImpl(String uri) {
        this.sender = Sender.builder().address(uri).build();
    }

    @Override
    public boolean insert(Telemetry telemetry) {
        try {
            sender.table("telemetries")
                    .symbol("type", telemetry.type())
                    .symbol("source", telemetry.source().name())
                    .stringColumn("vehicle", telemetry.vehicle())
                    .doubleColumn("value", telemetry.value())
                    .timestampColumn("originated_at", Timestamp.from(telemetry.originatedAt().toInstant()).getTime())
                    .atNow();
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    @Override
    public void clean() {
        this.sender.close();
    }
}
