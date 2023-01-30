package com.github.jmgoyesc.agent.adapters.questdb;

import com.github.jmgoyesc.agent.domain.models.Telemetry;
import com.github.jmgoyesc.agent.domain.services.ports.QuestdbPgPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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