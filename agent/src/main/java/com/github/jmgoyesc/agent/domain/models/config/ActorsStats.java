package com.github.jmgoyesc.agent.domain.models.config;

import lombok.Builder;
import lombok.Value;
import lombok.With;

/**
 * @author Juan Manuel Goyes Coral
 */

@Value
@Builder
public class ActorsStats {

    long success;
    long fail;
    long total;
    @With double throughput;
}
