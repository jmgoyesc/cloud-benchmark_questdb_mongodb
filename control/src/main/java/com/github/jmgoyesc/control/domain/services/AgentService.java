package com.github.jmgoyesc.control.domain.services;

import com.github.jmgoyesc.control.domain.models.agents.Agent;
import com.github.jmgoyesc.control.domain.models.agents.AgentResponse;
import com.github.jmgoyesc.control.domain.models.agents.AgentSignal;
import com.github.jmgoyesc.control.domain.models.ports.AgentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@RequiredArgsConstructor
public class AgentService {

    private final ExperimentService service;
    private final AgentPort port;
    private final CollectorService collector;

    public List<AgentResponse> create(List<Agent> agents) {
        service.addAgents(agents);
        return agents.stream()
                .map(a -> port.create(a)
                        .map(message -> AgentResponse.buildError(a.location(), message))
                        .orElseGet(() -> AgentResponse.buildDone(a.location())))
                .toList();
    }

    public List<AgentResponse> update(AgentSignal signal) {
        return switch (signal.action()) {
            case start -> start();
            case stop -> stop();
        };
    }

    private List<AgentResponse> start() {
        service.addExecutionStart(ZonedDateTime.now());
        return submitSignal(AgentSignal.Action.start);
    }

    private List<AgentResponse> stop() {
        var responses = submitSignal(AgentSignal.Action.stop);
        service.addExecutionEnd(ZonedDateTime.now());
        collector.collect(service.get().agents(), service);
        return responses;
    }

    private List<AgentResponse> submitSignal(AgentSignal.Action action) {
        return service.get().agents().stream()
            .map(Agent::location)
            .map(location -> port.put(location, action)
                    .map(message -> AgentResponse.buildError(location, message))
                    .orElseGet(() -> AgentResponse.buildDone(location)))
            .toList();
    }

}
