package com.github.jmgoyesc.agent.adapters.web.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

/**
 * @author Juan Manuel Goyes Coral
 */

@Validated
record Signal(
        @NotNull Type signal
) {
    public enum Type { start, stop }
}
