package com.github.jmgoyesc.control.adapters.web.controller;

import com.github.jmgoyesc.control.domain.services.AgentService;
import com.github.jmgoyesc.control.domain.services.TableService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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
@RequestMapping(value = "/v1/setups", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ControlController {

    private final TableService table;
    private final AgentService agent;

    //receive database connections, for each connection create table, return confirmation per database
    @PostMapping("/tables")
    public List<ConfirmationDTO> tables(@RequestBody @Validated @NotNull List<DatabaseConnectionDTO> databaseConnections) {
        var models = DatabaseConnectionDTO.toModel(databaseConnections);
        var response = table.create(models);
        return ConfirmationDTO.fromModel(response);
    }

    @PostMapping("/agents")
    public void agents() {
        //receive agent connection, setup each agent
    }

}
