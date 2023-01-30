package com.github.jmgoyesc.control.domain.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @author Juan Manuel Goyes Coral
 */

@Validated
public record AgentSignal(
        @NotNull List<@NotBlank String> locations,
        @NotNull Action action
) {
    public enum Action { start, stop }
}
