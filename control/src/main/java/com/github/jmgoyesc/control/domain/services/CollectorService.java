package com.github.jmgoyesc.control.domain.services;

import com.github.jmgoyesc.control.domain.models.agents.Agent;
import com.github.jmgoyesc.control.domain.models.experiment.Stats;
import com.github.jmgoyesc.control.domain.models.ports.AgentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@RequiredArgsConstructor
class CollectorService {

    private final AgentPort port;

    void collect(List<Agent> agents, ExperimentService service) {
        var groups = agents.parallelStream().collect(Collectors.groupingBy(Agent::type));

        groups.forEach((type, aggs) -> {
            var stats = aggs.parallelStream()
                    .map(it -> port.results(it.location()))
                    .reduce(
                            new Stats(0L, 0L),
                            (a,b) -> new Stats(a.telemetries() + b.telemetries(), a.error() + b.error()));
            service.addResultsInserted(type, stats);
        });
    }
}
