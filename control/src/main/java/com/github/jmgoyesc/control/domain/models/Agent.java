package com.github.jmgoyesc.control.domain.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

/**
 * @author Juan Manuel Goyes Coral
 */

@Validated
public record Agent(
        @NotBlank String location,
        @NotNull DatasourceType type,
        @NotBlank String uri
) {

    public enum DatasourceType { mongodb, questdb_pg, questdb_influx, questdb_rest }

}
