package com.github.jmgoyesc.agentquestdbrest.adapters.questdb;

import com.github.jmgoyesc.agentquestdbrest.domain.models.Telemetry;
import com.github.jmgoyesc.agentquestdbrest.domain.services.ports.QuestdbPgPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Log4j2
@RequiredArgsConstructor
class PgPortImpl implements QuestdbPgPort {

    private static final String SQL_INSERT = """
    INSERT INTO telemetries VALUES (?, ?, ?, ?, ?, ?)
    """;

    @Override
    public void insert(String uri, Telemetry telemetry) {
        try {
            final Connection connection = DriverManager.getConnection(uri);
            connection.setAutoCommit(true);

            try (PreparedStatement stmt = connection.prepareStatement(SQL_INSERT)) {
                stmt.setTimestamp(1, Timestamp.from(telemetry.receivedAt().toInstant()));
                stmt.setTimestamp(2, Timestamp.from(telemetry.originatedAt().toInstant()));
                stmt.setString(3, "pg-" + telemetry.vehicle());
                stmt.setString(4, telemetry.type());
                stmt.setDouble(5, telemetry.value());
                stmt.setNull(6, Types.OTHER);

                var res = stmt.execute();
                log.info("[questdb - pg] inserted record in telemetries tables: {}", res);
            }
            connection.close();
        } catch (SQLException e) {
            log.info("[questdb - pg] failed insertion", e);
        }
    }
}
