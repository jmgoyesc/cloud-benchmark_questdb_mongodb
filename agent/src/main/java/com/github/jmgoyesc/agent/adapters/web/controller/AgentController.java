package com.github.jmgoyesc.agent.adapters.web.controller;

import com.github.jmgoyesc.agent.domain.models.Config;
import com.github.jmgoyesc.agent.domain.services.AgentService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Juan Manuel Goyes Coral
 */

/*
TODO: manage handler
- 2023-01-14T12:25:58.202+01:00  WARN 6622 --- [nio-8080-exec-2] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.web.bind.MethodArgumentNotValidException: Validation failed for argument [0] in public void com.github.jmgoyesc.agentquestdbrest.adapters.web.controller.AgentController.configure(com.github.jmgoyesc.agentquestdbrest.domain.models.Config): [Field error in object 'config' on field 'uri': rejected value []; codes [NotBlank.config.uri,NotBlank.uri,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [config.uri,uri]; arguments []; default message [uri]]; default message [must not be blank]] ]
- 2023-01-14T12:51:25.432+01:00  WARN 9161 --- [nio-8080-exec-5] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.http.converter.HttpMessageNotReadableException: JSON parse error: Cannot deserialize value of type `com.github.jmgoyesc.agentquestdbrest.domain.models.Config$Database` from String "questdbi_rest": not one of the values accepted for Enum class: [mongodb, questdb_rest, questdb_influx, questdb_pg]]

 */

@RestController
@RequestMapping(value = "/v1/configurations", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AgentController {

    private final AgentService service;

    @PostMapping
    public void configure(@RequestBody @Validated @NotNull Config config) {
        service.configure(config);
    }

    @PatchMapping
    public void patch(@RequestBody @Validated @NotNull Signal signal) {
        switch (signal.signal()) {
            case start -> service.start();
            case stop -> service.stop();
        }
    }

}
