package com.github.jmgoyesc.agentquestdbrest.domain.models;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.ZonedDateTime;

/**
 * @author Juan Manuel Goyes Coral
 */

@Builder
@Jacksonized
public record Telemetry(
        ZonedDateTime receivedAt,
        ZonedDateTime originatedAt,
        String vehicle,
        String type,
        double value
) {
}
