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
import java.util.List;
import java.util.UUID;

import static com.github.jmgoyesc.agent.domain.models.config.TargetDB.questdb_influx;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * @author Juan Manuel Goyes Coral
 */

@DisplayName("UT. AgentControllerTest - Get All")
@WebMvcTest(AgentController.class)
@Import(JsonConfig.class)
class GetAllTest {

    @MockBean private AgentService service;
    @Autowired private MockMvc mvc;

    @DisplayName("T1. GIVEN service with valid configurations, WHEN call GET /v1/configurations, THEN return configuration found")
    @Test
    void test1() throws Exception {
        //given
        var id = UUID.fromString("00000000-0000-0000-0000-000000000000");
        var time = ZonedDateTime.of(1999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC);
        var connectionProperties = new Configuration.ConnectionProperties("url");
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
        given(service.all()).willReturn(List.of(configuration));

        //when
        mvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/v1/configurations")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON))
        //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json("""
                [
                    {
                        "id": "00000000-0000-0000-0000-000000000000",
                        "target": "questdb_influx",
                        "vehicles": 100,
                        "status": "CREATED",
                        "created_at": "1999-12-31T23:59:59.000Z",
                        "updated_at": "1999-12-31T23:59:59.000Z",
                        "connection": {
                            "url": "url",
                            "values": {}
                        }
                    }
                ]
                """, true));
    }
}
