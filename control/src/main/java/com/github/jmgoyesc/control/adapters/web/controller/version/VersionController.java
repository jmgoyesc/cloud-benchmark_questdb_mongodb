package com.github.jmgoyesc.control.adapters.web.controller.version;

import com.github.jmgoyesc.control.domain.models.versions.Versions;
import com.github.jmgoyesc.control.domain.services.VersionService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public Versions get(@RequestBody @Validated @NotNull List<String> locations) {
        return service.get(locations);
    }

}
