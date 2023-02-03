package com.github.jmgoyesc.control.adapters.mongodb;

import com.github.jmgoyesc.control.domain.models.ports.MongodbPort;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.TimeSeriesGranularity;
import com.mongodb.client.model.TimeSeriesOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Slf4j
class MongodbPortImpl implements MongodbPort {

    private static final String DATABASE_NAME = "benchmark";
    private static final String COLLECTION_NAME = "telemetries";

    //TODO: reduce timeout to 5 seconds
    //TODO: do a test changing the granularity among seconds, minutes and hours
    //TODO: add parameter "granularity" to create collection with different granularity
    @Override
    public Optional<String> create(String uri) {
        return withDatabase(uri, db -> {
            if (existsCollection(db)) {
                dropCollection(db);
            }
            createCollection(db);
        });
    }

    private Optional<String> withDatabase(String uri, Consumer<MongoDatabase> fn) {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            fn.accept(database);
            return Optional.empty();
        } catch (Exception e) {
            log.info("[mongodb] failed creating collection '{}' in database '{}'", COLLECTION_NAME, DATABASE_NAME, e);
            return Optional.of(StringUtils.defaultString(e.getMessage(), "no message available: %s".formatted(e.getClass())));
        }
    }

    private boolean existsCollection(MongoDatabase database) {
        return StreamSupport.stream(database.listCollectionNames().spliterator(), false)
                .anyMatch(it -> it.equals(COLLECTION_NAME));
    }

    private void dropCollection(MongoDatabase database) {
        log.info("[mongodb] Collection '{}' already exists and it will be dropped.", COLLECTION_NAME);
        database.getCollection(COLLECTION_NAME).drop();
    }

    private void createCollection(MongoDatabase database) {
        database.createCollection(COLLECTION_NAME, buildCollectionOptions());
        log.info("[mongodb] '{}' collection created in '{}' database", COLLECTION_NAME, DATABASE_NAME);
    }

    private static CreateCollectionOptions buildCollectionOptions() {
        var options = new CreateCollectionOptions();
        options.timeSeriesOptions(buildTimeSeriesOptions());
        return options;
    }

    private static TimeSeriesOptions buildTimeSeriesOptions() {
        var options = new TimeSeriesOptions("received_at");
        options.metaField("vehicle");
        options.granularity(TimeSeriesGranularity.SECONDS);
        return options;
    }
}
