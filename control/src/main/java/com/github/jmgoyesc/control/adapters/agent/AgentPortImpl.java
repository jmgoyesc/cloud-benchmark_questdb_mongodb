package com.github.jmgoyesc.control.adapters.agent;

import com.github.jmgoyesc.control.domain.models.Agent;
import com.github.jmgoyesc.control.domain.models.AgentSignal;
import com.github.jmgoyesc.control.domain.models.ports.AgentPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

/**
 * @author Juan Manuel Goyes Coral
 */
@Service
@RequiredArgsConstructor
@Slf4j
class AgentPortImpl implements AgentPort {

    private final RestTemplate rest;

    @Override
    public Optional<String> create(Agent agent) {
        var endpoint = UriComponentsBuilder.fromUriString(agent.location())
                .pathSegment("v1", "configurations")
                .toUriString();
        var config = new Config(agent.uri(), agent.type());
        try {
            rest.postForObject(endpoint, config, Void.class);
            return Optional.empty();
        } catch (RestClientException e) {
            log.info("[agent] Exception caught by creating.", e);
            return Optional.of(StringUtils.defaultString(e.getMessage(), "no message available: %s".formatted(e.getClass())));
        }
    }

    @Override
    public Optional<String> patch(String location, AgentSignal.Action signal) {
        var endpoint = UriComponentsBuilder.fromUriString(location)
                .pathSegment("v1", "configurations")
                .toUriString();
        var request = new Signal(signal);
        try {
            rest.patchForObject(endpoint, request, Void.class);
            return Optional.empty();
        } catch (RestClientException e) {
            log.info("[agent] Exception caught by patching.", e);
            return Optional.of(StringUtils.defaultString(e.getMessage(), "no message available: %s".formatted(e.getClass())));
        }
    }

}
