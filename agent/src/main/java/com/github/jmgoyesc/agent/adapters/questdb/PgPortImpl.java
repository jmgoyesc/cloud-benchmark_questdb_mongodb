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

    private static final String LOG_PREFIX = "[questdb - pg]";
    private static final String SQL_INSERT = """
    INSERT INTO telemetries
         ("received_at", "originated_at", "vehicle", "type", "source", "value", "position")
         VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

    //TODO: reuse connection (connection pool?)
    @Override
    public void insert(String uri, Telemetry telemetry) {
        try {
            final Connection connection = DriverManager.getConnection(uri);
            connection.setAutoCommit(true);

            try (PreparedStatement stmt = connection.prepareStatement(SQL_INSERT)) {
                stmt.setTimestamp(1, Timestamp.from(telemetry.receivedAt().toInstant()));
                stmt.setTimestamp(2, Timestamp.from(telemetry.originatedAt().toInstant()));
                stmt.setString(3, telemetry.vehicle());
                stmt.setString(4, telemetry.type());
                stmt.setString(5, telemetry.source().name());
                stmt.setDouble(6, telemetry.value());
                stmt.setNull(7, Types.OTHER);

                var count = stmt.executeUpdate();
                log.info("{} inserted record in telemetries tables. count: {}, telemetry: {}", LOG_PREFIX, count, telemetry);
            }
            connection.close();
        } catch (SQLException e) {
            log.info("{} failed insertion. telemetry: {}", LOG_PREFIX, telemetry, e);
        }
    }
}
