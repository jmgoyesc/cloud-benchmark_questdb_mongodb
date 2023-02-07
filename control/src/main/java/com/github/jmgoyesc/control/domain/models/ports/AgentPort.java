package com.github.jmgoyesc.control.domain.models.ports;

import com.github.jmgoyesc.control.domain.models.agents.Agent;
import com.github.jmgoyesc.control.domain.models.agents.AgentSignal;
import com.github.jmgoyesc.control.domain.models.experiment.Stats;
import com.github.jmgoyesc.control.domain.models.versions.VersionInfo;

import java.util.Optional;

/**
 * @author Juan Manuel Goyes Coral
 */

public interface AgentPort {
    Optional<String> create(Agent agent);
    Optional<String> put(String location, AgentSignal.Action signal);
    VersionInfo version(String location);
    Stats results(String location);
}
