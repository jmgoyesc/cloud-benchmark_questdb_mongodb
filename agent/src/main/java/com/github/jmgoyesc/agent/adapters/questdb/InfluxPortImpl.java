package com.github.jmgoyesc.agent.adapters.questdb;

import com.github.jmgoyesc.agent.domain.models.Telemetry;
import com.github.jmgoyesc.agent.domain.services.ports.QuestdbInfluxPort;
import io.questdb.client.Sender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Slf4j
@RequiredArgsConstructor
class InfluxPortImpl implements QuestdbInfluxPort {

    private static final String LOG_PREFIX = "[questdb - influx]";

    private final RestPortImpl rest;
    //TODO: sender is not thread safe and must be close after it is not used
    private Sender sender;

    @Override
    public void init(String uri) {
        this.sender = Sender.builder().address(uri).build();

        var endpoint = "http://" + uri.replace(":9009", ":9000");
        this.rest.init(endpoint);
    }

    //TODO: implement sender pool (close sender)
    @Override
    public void insert(Telemetry telemetry) {
        try {
            sender.table("telemetries")
                    .symbol("type", telemetry.type())
                    .symbol("source", telemetry.source().name())
                    .stringColumn("vehicle", telemetry.vehicle())
                    .doubleColumn("value", telemetry.value())
                    .timestampColumn("originated_at", Timestamp.from(telemetry.originatedAt().toInstant()).getTime())
                    .atNow();
            log.info("{} telemetry inserted", LOG_PREFIX);
        } catch (Throwable e) {
            log.error("{} exception inserting telemetry", LOG_PREFIX, e);
        }
    }

    @Override
    public long count() {
        // influx line protocol is a protocol only to insert data (one way protocol), not to get data
        // to implement count, it will be use the rest protocol to the correct uri format
        return rest.count();
    }

    @Override
    public void clean() {

    }
}
