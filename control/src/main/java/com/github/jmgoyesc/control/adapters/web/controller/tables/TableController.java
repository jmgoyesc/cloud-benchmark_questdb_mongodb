package com.github.jmgoyesc.control.adapters.web.controller.tables;

import com.github.jmgoyesc.control.domain.services.TableService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Juan Manuel Goyes Coral
 */

@RestController
@RequestMapping(value = "/v1/setups/tables", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class TableController {

    private final TableService service;

    //receive database connections, for each connection create table, return confirmation per database
    @PostMapping
    public List<ConfirmationDTO> create(@RequestBody @Validated @NotNull List<DatabaseConnectionDTO> databaseConnections) {
        log.info("[tables] Request for create received. Parameters: {}", databaseConnections);
        var models = DatabaseConnectionDTO.toModel(databaseConnections);
        var response = service.create(models);
        try {
            return ConfirmationDTO.fromModel(response);
        } finally {
            log.info("[tables] Request completed");
        }
    }

}
