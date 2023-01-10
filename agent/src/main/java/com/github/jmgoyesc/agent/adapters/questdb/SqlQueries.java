package com.github.jmgoyesc.agent.adapters.questdb;

/**
 * @author Juan Manuel Goyes Coral
 */

public class SqlQueries {

    public static final String SQL_INSERT = "INSERT INTO telemetries VALUES (?, ?, ?, ?, ?)";
    public static final String SQL_COUNT = "select count(1) from telemetries";
    public static final String SQL_TEST = "SHOW TABLES";
    public static final String SQL_CREATE = """
        CREATE TABLE IF NOT EXISTS telemetries (
            received_at TIMESTAMP,
            originated_at TIMESTAMP,
            vehicle STRING,
            value DOUBLE,
            position geohash(1c)
        ) timestamp(received_at)
    """;
    public static final String SQL_DROP = "DROP TABLE telemetries";
    public static final String SQL_DELETE = "TRUNCATE TABLE 'telemetries'";


}
