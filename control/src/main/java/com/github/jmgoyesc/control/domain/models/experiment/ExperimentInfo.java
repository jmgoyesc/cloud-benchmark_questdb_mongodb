package com.github.jmgoyesc.control.domain.models.experiment;

import com.github.jmgoyesc.control.domain.models.agents.Agent;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

/**
 * @author Juan Manuel Goyes Coral
 */

@Builder @Jacksonized
public record ExperimentInfo(
        Period tables,
        Period execution,
        Map<Agent.DatasourceType, Result> results
) {
    public ExperimentInfo() {
        this(new Period(), new Period(), Map.of());
    }
}
