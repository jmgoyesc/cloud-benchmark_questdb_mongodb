package com.github.jmgoyesc.agent.domain.models;

import com.github.jmgoyesc.agent.domain.models.Config.Database;
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
        Database source,
        double value
) {
}
