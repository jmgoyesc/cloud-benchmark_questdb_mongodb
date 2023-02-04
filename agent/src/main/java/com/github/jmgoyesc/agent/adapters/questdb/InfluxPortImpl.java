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

    private final ThreadLocal<Sender> sender = new ThreadLocal<>();
    private final RestPortImpl rest;

    //TODO: implement sender pool (close sender)
    @Override
    public void insert(String uri, Telemetry telemetry) {
        if (sender.get() == null) {
            log.info("{} Request new instance for sender", LOG_PREFIX);
            sender.set(Sender.builder().address(uri).build());
            log.info("{} Sender created", LOG_PREFIX);
        }

        try {
            sender.get().table("telemetries")
                    .symbol("type", telemetry.type())
                    .symbol("source", telemetry.source().name())
                    .stringColumn("vehicle", telemetry.vehicle())
                    .doubleColumn("value", telemetry.value())
                    .timestampColumn("originated_at", Timestamp.from(telemetry.originatedAt().toInstant()).getTime())
                    .atNow();
            log.info("{} inserted telemetry: {}", LOG_PREFIX, telemetry);
        } catch (Throwable e) {
            log.info("{} failed inserting telemetry: {}", LOG_PREFIX, telemetry, e);
        }
    }

    @Override
    public long count(String uri) {
        // influx line protocol is a protocol only to insert data (one way protocol), not to get data
        // to implement count, it will be use the rest protocol to the correct uri format
        var endpoint = "http://" + uri.replace(":9009", ":9000");
        return rest.count(endpoint);
    }
}
