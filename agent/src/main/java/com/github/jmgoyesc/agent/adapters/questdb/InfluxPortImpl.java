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
    @Override
    public void insert(String uri, Telemetry telemetry) {
        log.info("[questdb - influx] start inserting");
        try (Sender sender = Sender.builder().address(uri).build()) {

            sender.table("telemetries")
                    .symbol("type", telemetry.type())
//                    .timestampColumn("received_at", Timestamp.from(telemetry.receivedAt().toInstant()).getTime())
                    .timestampColumn("originated_at", Timestamp.from(telemetry.originatedAt().toInstant()).getTime())
                    .stringColumn("vehicle", "influx-" + telemetry.vehicle())
                    .doubleColumn("value", telemetry.value())
                    .atNow();
//                   //use this instead of received_at --> .atNow();
            log.info("[questdb - influx] telemetry inserted");

        } catch (Throwable e) {
            log.info("[questdb - influx] failed inserting telemetry", e);
        }
    }
}
