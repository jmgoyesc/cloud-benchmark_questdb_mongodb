package com.github.jmgoyesc.agent.adapters.mongodb;

import com.github.jmgoyesc.agent.domain.models.Telemetry;
import com.github.jmgoyesc.agent.domain.services.ports.MongodbPort;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCompressor;
import com.mongodb.client.MongoClients;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDateTime;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Slf4j
@RequiredArgsConstructor
class MongodbPortImpl implements MongodbPort {

    private static final String LOG_PREFIX = "[mongodb]";
    private static final String DATABASE_NAME = "benchmark";
    private static final String COLLECTION_NAME = "telemetries";

    private MongoClientSettings settings;

    @Override
    public void init(String uri) {
        this.settings = MongoClientSettings.builder()
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
    }

    @Override
    public void insert(Telemetry telemetry) {
        Document document = null;
        try (var client = MongoClients.create(this.settings)) {
            var coll = client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);
            document = build(telemetry);
            var res = coll.insertOne(document);
            log.info("{} => INSERTED. In {}.{} collection. ack:{}, id:{}, document: {}", LOG_PREFIX, DATABASE_NAME, COLLECTION_NAME, res.wasAcknowledged(), res.getInsertedId(), document);

        } catch (Exception e) {
            log.info("{} => FAILED. In {}.{} collection. document: {}", LOG_PREFIX, DATABASE_NAME, COLLECTION_NAME, document, e);
        }
    }

    @Override
    public long count() {
        try (var client = MongoClients.create(this.settings)) {
            return client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME).countDocuments();
        } catch (Exception e) {
            log.info("{} => FAILED. In {}.{} collection. Unable to count", LOG_PREFIX, DATABASE_NAME, COLLECTION_NAME, e);
            return 0L;
        }
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
