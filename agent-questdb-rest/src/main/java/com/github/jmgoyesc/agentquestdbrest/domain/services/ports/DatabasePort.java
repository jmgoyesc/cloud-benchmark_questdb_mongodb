package com.github.jmgoyesc.agentquestdbrest.domain.services.ports;

import com.github.jmgoyesc.agentquestdbrest.domain.models.Telemetry;

/**
 * @author Juan Manuel Goyes Coral
 */

public interface DatabasePort {
    void insert(String uri, Telemetry telemetry);
}
