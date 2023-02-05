package com.github.jmgoyesc.agent.domain.services.ports;

import com.github.jmgoyesc.agent.domain.models.Telemetry;

/**
 * @author Juan Manuel Goyes Coral
 */

public interface DatabasePort {
    void init(String uri);
    void insert(Telemetry telemetry);
    long count();
    default void clean() {}
}
