package com.github.jmgoyesc.control.domain.services;

import com.github.jmgoyesc.control.domain.models.ports.AgentPort;
import com.github.jmgoyesc.control.domain.models.versions.VersionInfo;
import com.github.jmgoyesc.control.domain.models.versions.Versions;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@RequiredArgsConstructor
public class VersionService {

    private final BuildProperties properties;
    private final AgentPort port;

    public Versions get(List<String> locations) {
        var agents = locations.stream()
                .map(location -> {
                    var version = port.version(location);
                    return Map.entry(location, version);
                })
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        return Versions.builder()
                .control(VersionInfo.build(properties))
                .agents(agents)
                .build();
    }

}