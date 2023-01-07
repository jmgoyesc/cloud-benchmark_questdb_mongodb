package com.github.jmgoyesc.agent.domain.models.config;

/**
 * @author Juan Manuel Goyes Coral
 */

public record DatabaseStatus(
        boolean closed,
        boolean running,
        boolean testQuery
) {
}
