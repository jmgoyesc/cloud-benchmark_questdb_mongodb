package com.github.jmgoyesc.control.adapters.mongodb;

import com.github.jmgoyesc.control.domain.models.ports.MongodbPort;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Slf4j
class MongodbPortImpl implements MongodbPort {

    //TODO: in case that the collection already exists, do not failed, and return success
    //TODO: reduce timeout to 5 seconds
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
