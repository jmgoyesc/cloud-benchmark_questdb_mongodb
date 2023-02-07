package com.github.jmgoyesc.agent.adapters.questdb;

import com.github.jmgoyesc.agent.domain.models.Telemetry;
import com.github.jmgoyesc.agent.domain.services.ports.QuestdbInfluxPort;
import io.questdb.client.Sender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Scope(SCOPE_PROTOTYPE)
@Slf4j
class InfluxPortImpl implements QuestdbInfluxPort {

    private static final long BUILDER_TIMEOUT = 3L;

    private final Sender.LineSenderBuilder builder;
    private Sender sender;

    public InfluxPortImpl(String uri) {
        this.builder = Sender.builder().address(uri);
    }

    @Override
    public boolean insert(Telemetry telemetry) {
        try {
            initSender();
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
        if (sender != null)
            this.sender.close();
    }
    private void initSender() {
        if (sender == null) {
            try (var executor = Executors.newSingleThreadExecutor()) {
                var future = executor.submit(builder::build);
                sender = future.get(BUILDER_TIMEOUT, TimeUnit.SECONDS);
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                log.error("[questdb influx] Unable to create Sender without {} seconds: {}", BUILDER_TIMEOUT, e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
