package com.github.jmgoyesc.control.domain.services;

import com.github.jmgoyesc.control.domain.models.Agent;
import com.github.jmgoyesc.control.domain.models.AgentSignal;
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

    public List<?> create(List<Agent> agents) {
        return agents.stream()
                .map(port::create)
                .toList();
    }

    public List<?> update(AgentSignal signal) {
        return signal.locations().stream()
                .map(it -> port.patch(it, signal.action()))
                .toList();
    }

}
