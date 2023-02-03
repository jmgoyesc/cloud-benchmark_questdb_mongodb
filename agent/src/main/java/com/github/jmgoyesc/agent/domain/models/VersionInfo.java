package com.github.jmgoyesc.agent.domain.models;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.boot.info.BuildProperties;

import java.time.Instant;

/**
 * @author Juan Manuel Goyes Coral
 */

@Builder
@Jacksonized
public record VersionInfo(
        String version,
        Instant time
) {

    public static VersionInfo build(BuildProperties props) {
        return VersionInfo.builder()
                .version(props.getVersion())
                .time(props.getTime())
                .build();
    }

}
