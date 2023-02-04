package com.github.jmgoyesc.control.domain.models.ports;

import com.github.jmgoyesc.control.domain.models.agents.Agent;

import java.util.List;

/**
 * @author Juan Manuel Goyes Coral
 */

public interface ExperimentConfigPort {
    void save(List<Agent> agents);
    List<Agent> get();
}
