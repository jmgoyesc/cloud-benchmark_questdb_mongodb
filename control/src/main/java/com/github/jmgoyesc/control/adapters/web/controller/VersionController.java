package com.github.jmgoyesc.control.adapters.web.controller;

import com.github.jmgoyesc.control.domain.models.versions.Versions;
import com.github.jmgoyesc.control.domain.services.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Juan Manuel Goyes Coral
 */

@RestController
@RequestMapping(value = "/v1/versions", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class VersionController {

    private final VersionService service;

    @GetMapping
    public Versions get() {
        return service.get();
    }

}
