package com.github.jmgoyesc.control.adapters.web.controller.agents;

import com.github.jmgoyesc.control.domain.models.Agent;
import com.github.jmgoyesc.control.domain.models.AgentSignal;
import com.github.jmgoyesc.control.domain.services.AgentService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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
public class AgentController {

    private final AgentService service;

    //receive agent connection, setup each agent
    @PostMapping
    public void create(@RequestBody @Validated @NotNull List<Agent> agents) {
        service.create(agents);
    }

    // start or stop all agents
    @PatchMapping
    public void update(@RequestBody @Validated @NotNull AgentSignal signal) {
        service.update(signal);
    }
}
