package com.github.jmgoyesc.agentquestdbrest.adapters.mongodb;

import com.github.jmgoyesc.agentquestdbrest.domain.models.Telemetry;
import com.github.jmgoyesc.agentquestdbrest.domain.services.ports.MongodbPort;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDateTime;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Slf4j
@RequiredArgsConstructor
class MongodbPortImpl implements MongodbPort {
    @Override
    public void insert(String uri, Telemetry telemetry) {
        try (MongoClient mongoClient = MongoClients.create(uri)) {

            MongoDatabase database = mongoClient.getDatabase("benchmark");
            var coll = database.getCollection("telemetries");
            var document = build(telemetry);
            var res = coll.insertOne(document);
            log.info("[mongodb] one record inserted in benchmark.telemetries collection. ack:{}, id:{}", res.wasAcknowledged(), res.getInsertedId());

        } catch (Exception e) {
            log.info("[mongodb] failed inserting telemetries collection", e);
        }
    }

    private static Document build(Telemetry telemetry) {
        var document = new Document();

        document.put("received_at", new BsonDateTime(Timestamp.from(telemetry.receivedAt().toInstant()).getTime()));
        document.put("originated_at", new BsonDateTime(Timestamp.from(telemetry.originatedAt().toInstant()).getTime()));
        document.put("vehicle", "mongodb-" + telemetry.vehicle());
        document.put("type", telemetry.type());
        document.put("value", telemetry.value());

        return document;
    }

}
