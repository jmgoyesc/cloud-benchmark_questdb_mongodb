package com.github.jmgoyesc.agent.adapters.questdb.postgres;

import com.github.jmgoyesc.agent.adapters.questdb.SqlQueries;
import com.github.jmgoyesc.agent.domain.models.biz.Telemetry;
import com.github.jmgoyesc.agent.domain.models.config.Configuration;
import com.github.jmgoyesc.agent.domain.models.config.DatabaseStatus;
import com.github.jmgoyesc.agent.domain.models.ports.QuestdbPostgresPort;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * @author Juan Manuel Goyes Coral
 */


@Service
@Scope(SCOPE_PROTOTYPE)
@Log4j2
class QuestdbPostgresPortImpl implements QuestdbPostgresPort {

    private HikariDataSource ds;

    @Override
    public void createdDS(Configuration configuration) {
        log.info("QuestDB Postgres createdDS: {}", configuration);

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(configuration.getConnection().url());
        config.setUsername(configuration.getConnection().values().get("user"));
        config.setPassword(configuration.getConnection().values().get("password"));

        this.ds = new HikariDataSource(config);
    }

    @Override
    public void close() {
        ds.close();
    }

    @Override
    public DatabaseStatus status() {
        return new DatabaseStatus(ds.isClosed(), ds.isRunning(), test());
    }

    @Override
    public void createTable() {
        execute(SqlQueries.SQL_CREATE);
    }

    @Override
    public void dropTable() {
        execute(SqlQueries.SQL_DROP);
    }

    @Override
    public void cleanData() {
        execute(SqlQueries.SQL_DELETE);
    }

    private boolean test() {
        try {
            execute(SqlQueries.SQL_TEST);
            return true;
        } catch (RuntimeException e) {
            log.warn("unable to execute test query", e);
            return false;
        }
    }

    @Override
    public String insert(Telemetry<?> telemetry) {
        try (
                var connection = ds.getConnection();
                var stmt = connection.prepareStatement(SqlQueries.SQL_INSERT)
        ) {
            var id = io.questdb.std.Os.currentTimeNanos();

            stmt.setTimestamp(1, new Timestamp(io.questdb.std.Os.currentTimeMicros()));
            stmt.setTimestamp(2, Timestamp.from(telemetry.originatedAt().toInstant()));
            stmt.setString(3, telemetry.vehicle());
            if (telemetry.value() instanceof Double value) {
                stmt.setDouble(4, value);
                stmt.setNull(5, Types.OTHER);
            } else {
                stmt.setNull(4, Types.DOUBLE);
                stmt.setNull(5, Types.OTHER);
            }
            //TODO: fix problem to save geojson as geo-hash
//            } else if (telemetry.value() instanceof Position value)
            stmt.executeUpdate();

            return String.valueOf(id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int count() {
        try (
                var connection = ds.getConnection();
                var stmt = connection.prepareStatement(SqlQueries.SQL_COUNT);
                var res = stmt.executeQuery()
        ) {

            res.next();
            return res.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void execute(String sql) {
        try (
                var connection = ds.getConnection();
                var stmt = connection.prepareStatement(sql)
        ) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
