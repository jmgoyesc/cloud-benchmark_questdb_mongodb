package com.github.jmgoyesc.control.domain.models.agents;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;


/**
 * @author Juan Manuel Goyes Coral
 */

@Validated
public record AgentSignal(
        @NotNull Action action
) {
    public enum Action { start, stop }
}
