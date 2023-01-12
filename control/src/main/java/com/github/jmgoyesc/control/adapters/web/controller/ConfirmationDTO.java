package com.github.jmgoyesc.control.adapters.web.controller;

import com.github.jmgoyesc.control.domain.models.Database;
import com.github.jmgoyesc.control.domain.models.DatabaseConnection;
import com.github.jmgoyesc.control.domain.models.Status;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * @author Juan Manuel Goyes Coral
 */

@Builder
@Jacksonized
record ConfirmationDTO(
        Database type,
        String uri,
        Status status,
        String error
) {

    static List<ConfirmationDTO> fromModel(List<DatabaseConnection> models) {
        return models.stream()
                .map(ConfirmationDTO::fromModel)
                .toList();
    }
    static ConfirmationDTO fromModel(DatabaseConnection model) {
        return ConfirmationDTO.builder()
                .type(model.type())
                .uri(model.uri())
                .status(model.status())
                .error(model.error())
                .build();
    }

}
