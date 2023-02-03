package com.github.jmgoyesc.control.domain.models.versions;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

/**
 * @author Juan Manuel Goyes Coral
 */

@Builder
@Jacksonized
public record Versions(
        VersionInfo control,
        Map<String, VersionInfo> agents
) { }
