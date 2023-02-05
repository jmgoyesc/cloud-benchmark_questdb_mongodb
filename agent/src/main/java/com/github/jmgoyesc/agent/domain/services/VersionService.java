package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.VersionInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class VersionService {

    private final BuildProperties properties;

    @PostConstruct
    public void init() {
        log.info("Agent app started. version: {} - build time: {}", properties.getVersion(), properties.getTime());
    }

    public VersionInfo get() {
        return VersionInfo.build(properties);
    }

}
