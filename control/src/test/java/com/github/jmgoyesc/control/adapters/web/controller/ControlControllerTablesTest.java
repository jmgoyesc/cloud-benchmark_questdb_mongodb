package com.github.jmgoyesc.control.adapters.web.controller;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.QuestDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Juan Manuel Goyes Coral
 */

@DisplayName("IT. ControlControllerTest - /tables")
@Testcontainers
@SpringBootTest
class ControlControllerTablesTest {

    @Container
    private static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo");

    @Container
    private static QuestDBContainer questDBContainer = new QuestDBContainer("questdb/questdb");

    @Autowired private WebApplicationContext context;
    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        assertNotNull(mongoDBContainer);
        assertNotNull(questDBContainer);
    }

    @DisplayName("T1. GIVEN a mongodb and a questdb connections, WHEN call POST /v1/setups/tables, THEN confirm tables got created")
    @Test
    void test1() throws Exception {
        //given
        var questdbUri = questDBContainer.getHttpUrl();
        var mongodbUri = mongoDBContainer.getConnectionString();

        //when
        mvc.perform(post("/v1/setups/tables")
                .contentType(APPLICATION_JSON)
                .content("""
                [
                    {
                        "type": "questdb",
                        "uri": "%s"
                    },
                    {
                        "type": "mongodb",
                        "uri": "%s"
                    }
                ]
                """.formatted(questdbUri, mongodbUri)))

        //then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json("""
                [
                    {
                        "type": "questdb",
                        "uri": "%s",
                        "status": "done"
                    },
                    {
                        "type": "mongodb",
                        "uri": "%s",
                        "status": "done"
                    }
                ]
                """.formatted(questdbUri, mongodbUri)));
        assertTableQuestdb();
        assertCollectionMongodb();
    }

    private static void assertTableQuestdb() {
        try (
                var stmt = questDBContainer.createConnection("").prepareStatement("SELECT count(1) FROM telemetries");
                var rs = stmt.executeQuery()
        ) {
            assertTrue(rs.next());
            var count = rs.getInt(1);
            assertEquals(0, count);
        } catch (SQLException | JdbcDatabaseContainer.NoDriverFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void assertCollectionMongodb() {
        var uri = mongoDBContainer.getConnectionString();
        try (MongoClient mongoClient = MongoClients.create(uri)) {

            MongoDatabase database = mongoClient.getDatabase("benchmark");
            var collection = database.getCollection("telemetries");

            var count = collection.countDocuments();
            assertEquals(0, count);

        }
    }

}