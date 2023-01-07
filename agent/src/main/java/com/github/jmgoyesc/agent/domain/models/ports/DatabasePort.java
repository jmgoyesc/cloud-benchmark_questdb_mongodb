package com.github.jmgoyesc.agent.domain.models.ports;

import com.github.jmgoyesc.agent.domain.models.config.Configuration;
import com.github.jmgoyesc.agent.domain.models.config.DatabaseStatus;
import com.github.jmgoyesc.agent.domain.models.biz.Telemetry;

/**
 * @author Juan Manuel Goyes Coral
 */

public interface DatabasePort {

    void createdDS(Configuration configuration);
    void close();
    DatabaseStatus status();
    void createTable();
    void dropTable();
    void cleanData();
    String insert(Telemetry<?> telemetry);
    int count();

}
