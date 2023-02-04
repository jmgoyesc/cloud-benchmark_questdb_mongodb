package com.github.jmgoyesc.control.adapters.experiment;

import com.github.jmgoyesc.control.domain.models.agents.Agent;
import com.github.jmgoyesc.control.domain.models.ports.ExperimentConfigPort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
class InMemoryExperimentConfigPort implements ExperimentConfigPort {

    private List<Agent> agents = List.of();

    @Override
    public void save(List<Agent> agents) {
        this.agents = agents;
    }

    @Override
    public List<Agent> get() {
        return this.agents;
    }
}
