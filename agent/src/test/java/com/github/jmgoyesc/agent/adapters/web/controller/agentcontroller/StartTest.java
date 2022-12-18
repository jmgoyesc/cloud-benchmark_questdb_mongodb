package com.github.jmgoyesc.agent.adapters.web.controller.agentcontroller;

import com.github.jmgoyesc.agent.adapters.web.controller.AgentController;
import com.github.jmgoyesc.agent.application.JsonConfig;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * @author Juan Manuel Goyes Coral
 */

@DisplayName("UT. AgentControllerTest - Start")
@WebMvcTest(AgentController.class)
@Import(JsonConfig.class)
class StartTest {

    @MockBean private AgentService service;
    @Autowired private MockMvc mvc;

    @DisplayName("T1. GIVEN a configuration id, WHEN call POST /v1/configurations/{id}/start, THEN call service.start")
    @Test
    void test1() throws Exception {
        //given
        willDoNothing().given(service).start(eq("00000000-0000-0000-0000-000000000000"));

        //when
        mvc.perform(MockMvcRequestBuilders.request(HttpMethod.POST, "/v1/configurations/00000000-0000-0000-0000-000000000000/start")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON))
        //then
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
