package com.github.jmgoyesc.agent.adapters.mongodb;

import com.github.jmgoyesc.agent.domain.models.Telemetry;
import com.github.jmgoyesc.agent.domain.services.ports.MongodbPort;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDateTime;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

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

    //TODO: Keep MongoDatabase active (faster?)
    @Override
    public void insert(String uri, Telemetry telemetry) {
        Document document = null;
        try (MongoClient mongoClient = MongoClients.create(uri)) {

            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            var coll = database.getCollection(COLLECTION_NAME);
            document = build(telemetry);
            var res = coll.insertOne(document);
            log.info("{} => INSERTED. In {}.{} collection. ack:{}, id:{}, document: {}", LOG_PREFIX, DATABASE_NAME, COLLECTION_NAME, res.wasAcknowledged(), res.getInsertedId(), document);

        } catch (Exception e) {
            log.info("{} => FAILED. In {}.{} collection. document: {}", LOG_PREFIX, DATABASE_NAME, COLLECTION_NAME, document, e);
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
