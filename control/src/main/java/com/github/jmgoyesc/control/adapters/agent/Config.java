package com.github.jmgoyesc.control.adapters.agent;

import com.github.jmgoyesc.control.domain.models.Agent.DatasourceType;

/**
 * @author Juan Manuel Goyes Coral
 */

record Config(
        String uri,
        DatasourceType db) {
}
