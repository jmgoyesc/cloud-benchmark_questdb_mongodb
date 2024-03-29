package com.github.jmgoyesc.control.domain.models.agents;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

/**
 * @author Juan Manuel Goyes Coral
 */

@Validated
public record Agent(
        @NotBlank String location,
        @NotNull DatasourceType type,
        @NotBlank String datasource,
        @Positive int vehicles
) {

    public enum DatasourceType { mongodb, questdb_pg, questdb_influx, questdb_rest }

}
