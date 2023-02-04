package com.github.jmgoyesc.control.domain.models.ports;

import com.github.jmgoyesc.control.domain.models.experiment.ExperimentInfo;

/**
 * @author Juan Manuel Goyes Coral
 */

public interface ExperimentInfoPort {
    ExperimentInfo get();
    void update(ExperimentInfo info);

}
