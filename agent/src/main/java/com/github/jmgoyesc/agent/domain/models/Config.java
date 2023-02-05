package com.github.jmgoyesc.agent.domain.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

/**
 * @author Juan Manuel Goyes Coral
 */

@Validated
public record Config(
        @NotBlank String uri,
        @NotNull Database db,
        @Positive int vehicles
) {

    public enum Database { mongodb, questdb_pg, questdb_influx, questdb_rest }

}
