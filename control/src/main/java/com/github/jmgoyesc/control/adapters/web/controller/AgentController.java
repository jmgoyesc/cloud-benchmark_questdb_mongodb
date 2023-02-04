package com.github.jmgoyesc.control.adapters.web.controller;

import com.github.jmgoyesc.control.domain.models.agents.Agent;
import com.github.jmgoyesc.control.domain.models.agents.AgentResponse;
import com.github.jmgoyesc.control.domain.models.agents.AgentSignal;
import com.github.jmgoyesc.control.domain.services.AgentService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Juan Manuel Goyes Coral
 */

@RestController
@RequestMapping(value = "/v1/setups/agents", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class AgentController {

    private final AgentService service;

    //receive agent connection, setup each agent
    @PostMapping
    public List<AgentResponse> create(@RequestBody @Validated @NotNull List<Agent> agents) {
        log.info("[POST agents] Request received. Parameters: {}", agents);
        var response = service.create(agents);
        log.info("[POST agents] Request completed. Response: {}", response);
        return response;
    }

    // start or stop all agents
    @PatchMapping
    public List<AgentResponse> update(@RequestBody @Validated @NotNull AgentSignal signal) {
        log.info("[PATCH agents] Request received. Parameters: {}", signal);
        var response = service.update(signal);
        log.info("[PATCH agents] Request completed. Response: {}", response);
        return response;
    }
}
