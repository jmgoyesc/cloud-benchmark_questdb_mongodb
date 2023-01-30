package com.github.jmgoyesc.control.domain.models.ports;

import com.github.jmgoyesc.control.domain.models.Agent;
import com.github.jmgoyesc.control.domain.models.AgentSignal;

import java.util.Optional;

/**
 * @author Juan Manuel Goyes Coral
 */

public interface AgentPort {
    Optional<String> create(Agent agent);
    Optional<String> patch(String location, AgentSignal.Action signal);
}
