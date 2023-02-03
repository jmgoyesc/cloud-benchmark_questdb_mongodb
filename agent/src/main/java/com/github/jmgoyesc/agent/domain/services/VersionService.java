package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.VersionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@RequiredArgsConstructor
public class VersionService {

    private final BuildProperties properties;

    public VersionInfo get() {
        return VersionInfo.build(properties);
    }

}
