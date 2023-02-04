package com.github.jmgoyesc.control.domain.models.experiment;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * @author Juan Manuel Goyes Coral
 */

@Builder @Jacksonized
public record ResultDuration(
        long totalInSeconds,
        long totalInMinutes,
        long totalInHours
) {
}
