package com.github.jmgoyesc.control.adapters.web.controller;

import com.github.jmgoyesc.control.domain.models.experiment.ExperimentInfo;
import com.github.jmgoyesc.control.domain.services.ExperimentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Juan Manuel Goyes Coral
 */

@RestController
@RequestMapping(value = "/v1/experiments", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class ExperimentInfoController {

    private final ExperimentService service;

    @GetMapping
    public ExperimentInfo get() {
        var info = service.get();
        log.info("Experiment info requested: {}", info);
        return info;
    }

}
