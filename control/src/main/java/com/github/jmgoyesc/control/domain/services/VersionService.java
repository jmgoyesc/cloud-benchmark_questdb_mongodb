package com.github.jmgoyesc.control.domain.services;

import com.github.jmgoyesc.control.domain.models.agents.Agent;
import com.github.jmgoyesc.control.domain.models.ports.AgentPort;
import com.github.jmgoyesc.control.domain.models.versions.VersionInfo;
import com.github.jmgoyesc.control.domain.models.versions.Versions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class VersionService {

    private final BuildProperties properties;
    private final AgentPort port;
    private final ExperimentService service;

    @PostConstruct
    public void init() {
        log.info("Control app started. version: {} - build time: {}", properties.getVersion(), properties.getTime());
    }

    public Versions get() {
        var agents = service.get().agents().parallelStream()
                .map(Agent::location)
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
