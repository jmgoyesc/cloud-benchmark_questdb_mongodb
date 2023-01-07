package com.github.jmgoyesc.agent.adapters.web.controller;

import com.github.jmgoyesc.agent.domain.models.config.Configuration.ConnectionProperties;
import com.github.jmgoyesc.agent.domain.models.config.TargetDB;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

/**
 * @author Juan Manuel Goyes Coral
 */

@Validated
record AgentDTO(
        @NotNull TargetDB target,
        @Positive int vehicles,
        @NotNull ConnectionProperties connection
) {
}
