package com.github.jmgoyesc.control.domain.models.tables;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * @author Juan Manuel Goyes Coral
 */

@Builder
@Jacksonized
public record DatabaseConnection(
        Database type,
        String uri,
        Status status,
        String error
) {
}
