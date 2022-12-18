package com.github.jmgoyesc.agent.adapters.web.controller.agentcontroller;

import com.github.jmgoyesc.agent.adapters.web.controller.AgentController;
import com.github.jmgoyesc.agent.application.JsonConfig;
import com.github.jmgoyesc.agent.domain.models.Configuration;
import com.github.jmgoyesc.agent.domain.services.AgentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

import static com.github.jmgoyesc.agent.domain.models.Configuration.Status.CREATED;
import static com.github.jmgoyesc.agent.domain.models.Configuration.TargetDB.questdb_ilp;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * @author Juan Manuel Goyes Coral
 */

@DisplayName("UT. AgentControllerTest - Create")
@WebMvcTest(AgentController.class)
@Import(JsonConfig.class)
class CreateTest {

    @MockBean private AgentService service;
    @Autowired private MockMvc mvc;

    @DisplayName("T1. GIVEN a target and vehicles number, WHEN call POST /v1/configurations, THEN create new configuration")
    @Test
    void test1() throws Exception {
        //given
        var time = ZonedDateTime.of(1999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC);
        var connectionProperties = new Configuration.ConnectionProperties("url", Map.of("username", "admin", "password", "secret"));
        var configuration = new Configuration("00000000-0000-0000-0000-000000000000", questdb_ilp, 100, CREATED, time, time, connectionProperties);
        given(service.create(eq(questdb_ilp), eq(100), eq(connectionProperties))).willReturn(configuration);

        //when
        mvc.perform(MockMvcRequestBuilders.request(HttpMethod.POST, "/v1/configurations")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content("""
                        { "target": "questdb_ilp", "vehicles": 100, "connection": { "url": "url", "values": { "username": "admin", "password": "secret" } } }
                        """))
        //then
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json("""
                {
                    "id": "00000000-0000-0000-0000-000000000000",
                    "target": "questdb_ilp",
                    "vehicles": 100,
                    "status": "CREATED",
                    "created_at": "1999-12-31T23:59:59.000Z",
                    "updated_at": "1999-12-31T23:59:59.000Z",
                    "connection": {
                        "url": "url",
                        "values": {
                            "username": "admin",
                            "password": "secret"
                        }
                    }
                }
                """, true));
    }
}
