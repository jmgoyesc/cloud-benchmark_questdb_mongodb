package com.github.jmgoyesc.control.domain.services;

import com.github.jmgoyesc.control.domain.models.agents.Agent;
import com.github.jmgoyesc.control.domain.models.agents.AgentResponse;
import com.github.jmgoyesc.control.domain.models.agents.AgentSignal;
import com.github.jmgoyesc.control.domain.models.ports.AgentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentPort port;

    public List<AgentResponse> create(List<Agent> agents) {
        return agents.stream()
                .map(a -> port.create(a)
                        .map(message -> AgentResponse.buildError(a.location(), message))
                        .orElseGet(() -> AgentResponse.buildDone(a.location())))
                .toList();
    }

    public List<AgentResponse> update(AgentSignal signal) {
        return signal.locations().stream()
                .map(location -> port.patch(location, signal.action())
                        .map(message -> AgentResponse.buildError(location, message))
                        .orElseGet(() -> AgentResponse.buildDone(location)))
                .toList();
    }

}
