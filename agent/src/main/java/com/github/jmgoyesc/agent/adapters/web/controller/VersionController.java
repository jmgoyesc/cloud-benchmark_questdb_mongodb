package com.github.jmgoyesc.agent.adapters.web.controller;

import com.github.jmgoyesc.agent.domain.models.VersionInfo;
import com.github.jmgoyesc.agent.domain.services.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Juan Manuel Goyes Coral
 */

@RestController
@RequestMapping(value = "/v1/version", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class VersionController {

    private final VersionService service;

    @GetMapping
    public VersionInfo get() {
        return service.get();
    }

}
