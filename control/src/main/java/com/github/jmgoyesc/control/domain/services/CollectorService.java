package com.github.jmgoyesc.control.domain.services;

import com.github.jmgoyesc.control.domain.models.agents.Agent;
import com.github.jmgoyesc.control.domain.models.ports.AgentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@RequiredArgsConstructor
class CollectorService {

    private final AgentPort port;

    void collect(List<Agent> agents, ExperimentService service) {
        agents.forEach(it -> {
                    var inserted = port.results(it.location());
                    service.addResultsInserted(it.type(), inserted);
                });
    }
}
