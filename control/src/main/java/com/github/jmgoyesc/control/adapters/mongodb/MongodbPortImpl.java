package com.github.jmgoyesc.control.adapters.mongodb;

import com.github.jmgoyesc.control.domain.models.ports.MongodbPort;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Log4j2
class MongodbPortImpl implements MongodbPort {

    @Override
    public Optional<String> create(String uri) {
        try (MongoClient mongoClient = MongoClients.create(uri)) {

            MongoDatabase database = mongoClient.getDatabase("benchmark");
            database.createCollection("telemetries");
            log.info("[mongodb] telemetries collection created in benchmark database");
            return Optional.empty();

        } catch (Exception e) {
            log.info("[mongodb] failed creating telemetries collection", e);
            return Optional.of(StringUtils.defaultString(e.getMessage(), "no message available: %s".formatted(e.getClass())));
        }
    }
}
