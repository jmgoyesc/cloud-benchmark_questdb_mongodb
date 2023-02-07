package com.github.jmgoyesc.agent.adapters.mongodb;

import com.github.jmgoyesc.agent.domain.models.Telemetry;
import com.github.jmgoyesc.agent.domain.services.ports.MongodbPort;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCompressor;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDateTime;
import org.bson.Document;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Scope(SCOPE_PROTOTYPE)
@Slf4j
class MongodbPortImpl implements MongodbPort {

    private static final String DATABASE_NAME = "benchmark";
    private static final String COLLECTION_NAME = "telemetries";

    private final MongoClient client;

    public MongodbPortImpl(String uri) {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .applyToSocketSettings(builder -> builder
                        .connectTimeout(3, TimeUnit.SECONDS)
                        .readTimeout(3, TimeUnit.SECONDS))
                .applyToConnectionPoolSettings(builder -> builder
                        .minSize(10)
                        .maxWaitTime(3, TimeUnit.SECONDS))
                .compressorList(List.of(MongoCompressor.createSnappyCompressor()))
                .applyToSslSettings(builder -> builder
                        .enabled(false))
                .build();
        this.client = MongoClients.create(settings);
    }

    @Override
    public boolean insert(Telemetry telemetry) {
        try {
            var coll = client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);
            var document = build(telemetry);
            coll.insertOne(document);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void clean() {
        this.client.close();
    }

    private static Document build(Telemetry telemetry) {
        var document = new Document();

        document.put("received_at", convert(telemetry.receivedAt()));
        document.put("originated_at", convert(telemetry.originatedAt()));
        document.put("vehicle", telemetry.vehicle());
        document.put("type", telemetry.type());
        document.put("source", telemetry.source());
        document.put("value", telemetry.value());

        return document;
    }

    private static BsonDateTime convert(ZonedDateTime time) {
        return new BsonDateTime(Timestamp.from(time.toInstant()).getTime());
    }

}
