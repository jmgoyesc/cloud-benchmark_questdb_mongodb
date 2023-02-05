package com.github.jmgoyesc.control.adapters.experiment;

import com.github.jmgoyesc.control.domain.models.experiment.ExperimentInfo;
import com.github.jmgoyesc.control.domain.models.ports.ExperimentInfoPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Slf4j
class InMemoryExperimentInfoPort implements ExperimentInfoPort {

    private ExperimentInfo info = new ExperimentInfo();

    @Override
    public ExperimentInfo get() {
        return this.info;
    }

    @Override
    public void update(ExperimentInfo info) {
        this.info = info;
        log.info("[in-memory-experiment] Experiment updated: {}", info);
    }
}
