package com.github.jmgoyesc.agent.adapters.questdb;

import com.github.jmgoyesc.agent.domain.models.Telemetry;
import com.github.jmgoyesc.agent.domain.services.ports.QuestdbPgPort;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Scope(SCOPE_PROTOTYPE)
@Slf4j
class PgPortImpl implements QuestdbPgPort {

    private static final String SQL_INSERT = """
    INSERT INTO telemetries
         ("received_at", "originated_at", "vehicle", "type", "source", "value", "position")
         VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

    private final HikariDataSource ds;

    public PgPortImpl(String uri) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(uri);
        config.setAutoCommit(true);
        config.setConnectionTimeout(3_000);
        config.setInitializationFailTimeout(3_000);
        this.ds = new HikariDataSource(config);
    }

    @Override
    public boolean insert(Telemetry telemetry) {
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
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void clean() {
        ds.close();
    }

}
