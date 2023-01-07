package com.github.jmgoyesc.agent.adapters.web.controller.agentcontroller;

import com.github.jmgoyesc.agent.adapters.web.controller.AgentController;
import com.github.jmgoyesc.agent.application.JsonConfig;
import com.github.jmgoyesc.agent.domain.models.config.Configuration;
import com.github.jmgoyesc.agent.domain.services.AgentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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
import java.util.UUID;

import static com.github.jmgoyesc.agent.domain.models.config.Configuration.Status.CREATED;
import static com.github.jmgoyesc.agent.domain.models.config.TargetDB.questdb_influx;
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
        var id = UUID.fromString("00000000-0000-0000-0000-000000000000");
        var time = ZonedDateTime.of(1999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC);
        var connectionProperties = new Configuration.ConnectionProperties("url", Map.of("username", "admin", "password", "secret"));
        Configuration configuration;
        try (
                MockedStatic<UUID> uuidMock = Mockito.mockStatic(UUID.class);
                MockedStatic<ZonedDateTime> timeMock = Mockito.mockStatic(ZonedDateTime.class)
            ) {
            //noinspection ResultOfMethodCallIgnored
            uuidMock.when(UUID::randomUUID).thenReturn(id);
            timeMock.when(ZonedDateTime::now).thenReturn(time);
            configuration = Configuration.builder()
                    .target(questdb_influx)
                    .vehicles(100)
                    .connection(connectionProperties)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        given(service.create(eq(questdb_influx), eq(100), eq(connectionProperties))).willReturn(configuration);

        //when
        mvc.perform(MockMvcRequestBuilders.request(HttpMethod.POST, "/v1/configurations")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content("""
                        { "target": "questdb_influx", "vehicles": 100, "connection": { "url": "url", "values": { "username": "admin", "password": "secret" } } }
                        """))
        //then
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json("""
                {
                    "id": "00000000-0000-0000-0000-000000000000",
                    "target": "questdb_influx",
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
