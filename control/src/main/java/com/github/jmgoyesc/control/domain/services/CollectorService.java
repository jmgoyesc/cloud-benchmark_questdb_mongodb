package com.github.jmgoyesc.control.domain.services;

import com.github.jmgoyesc.control.domain.models.ports.AgentPort;
import com.github.jmgoyesc.control.domain.models.ports.ExperimentConfigPort;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@RequiredArgsConstructor
class CollectorService {

    private final ExperimentService service;
    private final ExperimentConfigPort config;
    private final AgentPort port;

    @Async
    void collect() {
        service.addExecutionEnd(ZonedDateTime.now());
        config.get()
                .forEach(it -> {
                    var inserted = port.countInserted(it.location());
                    service.addResultsInserted(it.type(), inserted);
                });
    }
}
