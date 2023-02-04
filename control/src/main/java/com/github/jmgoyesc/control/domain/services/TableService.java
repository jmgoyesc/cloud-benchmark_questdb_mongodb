package com.github.jmgoyesc.control.domain.services;

import com.github.jmgoyesc.control.domain.models.tables.DatabaseConnection;
import com.github.jmgoyesc.control.domain.models.tables.Status;
import com.github.jmgoyesc.control.domain.models.ports.MongodbPort;
import com.github.jmgoyesc.control.domain.models.ports.QuestdbPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@RequiredArgsConstructor
public class TableService {

    private final ExperimentService service;
    private final MongodbPort mongodbPort;
    private final QuestdbPort questdbPort;

    public List<DatabaseConnection> create(List<DatabaseConnection> databaseConnections) {
        service.addTablesStart(ZonedDateTime.now());
        var output = databaseConnections.parallelStream()
                .map(it -> {
                    //noinspection CodeBlock2Expr
                    return switch (it.type()) {
                        case mongodb -> buildResponse(it, mongodbPort.create(it.uri()));
                        case questdb -> buildResponse(it, questdbPort.create(it.uri()));
                    };
                })
                .toList();
        service.addTablesEnd(ZonedDateTime.now());
        return output;
    }

    private static DatabaseConnection buildResponse(DatabaseConnection input, Optional<String> error) {
        return DatabaseConnection.builder()
                .type(input.type())
                .uri(input.uri())
                .status(error.isEmpty() ? Status.done : Status.error)
                .error(error.orElse(null))
                .build();
    }

}
