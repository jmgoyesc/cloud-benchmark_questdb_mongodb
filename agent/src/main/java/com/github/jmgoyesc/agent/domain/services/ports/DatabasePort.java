package com.github.jmgoyesc.agent.domain.services.ports;

import com.github.jmgoyesc.agent.domain.models.Telemetry;

/**
 * @author Juan Manuel Goyes Coral
 */

public interface DatabasePort {
    boolean insert(Telemetry telemetry);
    void clean();
}
