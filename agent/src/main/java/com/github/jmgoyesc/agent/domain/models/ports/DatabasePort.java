package com.github.jmgoyesc.agent.domain.models.ports.ports;

import com.github.jmgoyesc.agent.domain.models.Configuration;
import com.github.jmgoyesc.agent.domain.models.DatabaseStatus;
import com.github.jmgoyesc.agent.domain.models.Telemetry;
import com.github.jmgoyesc.agent.domain.models.Vehicle;

/**
 * @author Juan Manuel Goyes Coral
 */

public interface DatabasePort {

    void connect(Configuration configuration);
    DatabaseStatus status();
    void insert(Vehicle vehicle);
    void insert(Telemetry telemetry);

}
