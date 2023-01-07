package com.github.jmgoyesc.agent.adapters.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.jmgoyesc.agent.application.JsonConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static com.github.jmgoyesc.agent.domain.models.config.TargetDB.questdb_influx;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Juan Manuel Goyes Coral
 */

@DisplayName("UT. AgentDTOTest.")
@ExtendWith(MockitoExtension.class)
class AgentDTOTest {

    @DisplayName("T1. GIVEN json, WHEN mapper readValue, THEN get AgentDTO")
    @Test
    void test1() throws JsonProcessingException {
        //given
        var json = """
        {
            "target": "questdb_influx",
            "vehicles": 100,
            "connection": {
                "url": "url",
                "values": {
                    "username": "username",
                    "password": "password"
                }
            }
        }
        """;

        //when
        var actual = JsonConfig.mapper().readValue(json, AgentDTO.class);

        //then
        assertThat(actual)
                .as("Check AgentDTO got created from json")
                .isNotNull()
                .hasFieldOrPropertyWithValue("target", questdb_influx)
                .hasFieldOrPropertyWithValue("vehicles", 100)
                .hasFieldOrPropertyWithValue("connection.url", "url")
                .hasFieldOrPropertyWithValue("connection.values", Map.of("username", "username", "password", "password"));
    }

}