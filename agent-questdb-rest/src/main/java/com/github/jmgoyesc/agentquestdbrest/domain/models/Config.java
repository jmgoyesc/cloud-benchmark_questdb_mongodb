package com.github.jmgoyesc.agentquestdbrest.domain.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

/**
 * @author Juan Manuel Goyes Coral
 */

@Validated
public record Config(
        @NotBlank String uri,
        @NotNull Database db
) {

    public enum Database { mongodb, questdb_pg, questdb_influx, questdb_rest }

}
