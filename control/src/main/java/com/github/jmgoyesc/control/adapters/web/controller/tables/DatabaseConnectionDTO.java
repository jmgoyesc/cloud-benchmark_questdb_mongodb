package com.github.jmgoyesc.control.adapters.web.controller.tables;

import com.github.jmgoyesc.control.domain.models.tables.Database;
import com.github.jmgoyesc.control.domain.models.tables.DatabaseConnection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @author Juan Manuel Goyes Coral
 */

@Validated
record DatabaseConnectionDTO(
        @NotNull Database type,
        @NotBlank String uri
) {

    static List<DatabaseConnection> toModel(List<DatabaseConnectionDTO> dtos) {
        return dtos.stream()
                .map(DatabaseConnectionDTO::toModel)
                .toList();
    }
    static DatabaseConnection toModel(DatabaseConnectionDTO dto) {
        return DatabaseConnection.builder()
                .type(dto.type)
                .uri(dto.uri)
                .build();
    }

}
