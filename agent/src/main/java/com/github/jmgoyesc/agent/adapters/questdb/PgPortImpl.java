package com.github.jmgoyesc.agent.adapters.questdb;

import com.github.jmgoyesc.agent.domain.models.Telemetry;
import com.github.jmgoyesc.agent.domain.services.ports.QuestdbPgPort;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    private HikariDataSource ds;

    private static final String LOG_PREFIX = "[questdb - pg]";
    private static final String SQL_INSERT = """
    INSERT INTO telemetries
         ("received_at", "originated_at", "vehicle", "type", "source", "value", "position")
         VALUES (?, ?, ?, ?, ?, ?, ?)
    """;
    private static final String SQL_COUNT = """
    SELECT count() from telemetries
    """;

    @Override
    public void init(String uri) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(uri);
        config.setAutoCommit(true);
        config.setConnectionTimeout(3_000);
        config.setInitializationFailTimeout(3_000);
        this.ds = new HikariDataSource(config);
    }

    @Override
    public void insert(Telemetry telemetry) {
        try (var conn = ds.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
                stmt.setTimestamp(1, Timestamp.from(telemetry.receivedAt().toInstant()));
                stmt.setTimestamp(2, Timestamp.from(telemetry.originatedAt().toInstant()));
                stmt.setString(3, telemetry.vehicle());
                stmt.setString(4, telemetry.type());
                stmt.setString(5, telemetry.source().name());
                stmt.setDouble(6, telemetry.value());
                stmt.setNull(7, Types.OTHER);

                stmt.executeUpdate();
                log.info("{} telemetry inserted", LOG_PREFIX);
            }
        } catch (SQLException e) {
            log.error("{} exception inserting telemetry", LOG_PREFIX, e);
        }
    }

    @Override
    public long count() {
        var count = 0L;
        try (var conn = ds.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(SQL_COUNT)) {
                var rs = stmt.executeQuery();
                rs.next();
                count = rs.getLong(1);
            }
        } catch (SQLException e) {
            log.error("{} exception counting telemetry", LOG_PREFIX, e);
        }
        return count;
    }

}
