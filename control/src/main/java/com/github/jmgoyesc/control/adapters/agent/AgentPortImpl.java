package com.github.jmgoyesc.control.adapters.agent;

import com.github.jmgoyesc.control.domain.models.agents.Agent;
import com.github.jmgoyesc.control.domain.models.agents.AgentSignal;
import com.github.jmgoyesc.control.domain.models.ports.AgentPort;
import com.github.jmgoyesc.control.domain.models.versions.VersionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.HttpMethod.GET;

/**
 * @author Juan Manuel Goyes Coral
 */
@Service
@RequiredArgsConstructor
@Slf4j
class AgentPortImpl implements AgentPort {

    private static final String LOG_PREFIX = "[agent]";

    private final RestTemplate rest;

    @Override
    public Optional<String> create(Agent agent) {
        var endpoint = buildEndpoint(agent.location());
        var config = new Config(agent.datasource(), agent.type(), agent.vehicles());
        try {
            rest.postForObject(endpoint, config, Void.class);
            return Optional.empty();
        } catch (RestClientException e) {
            log.info("{} Exception caught by creating.", LOG_PREFIX, e);
            return Optional.of(StringUtils.defaultString(e.getMessage(), "no message available: %s".formatted(e.getClass())));
        }
    }

    @Override
    public Optional<String> put(String location, AgentSignal.Action signal) {
        var endpoint = buildEndpoint(location);
        var request = new Signal(signal);
        try {
            rest.put(endpoint, request);
            return Optional.empty();
        } catch (RestClientException e) {
            log.info("{} Exception caught by patching.", LOG_PREFIX, e);
            return Optional.of(StringUtils.defaultString(e.getMessage(), "no message available: %s".formatted(e.getClass())));
        }
    }

    @Override
    public VersionInfo version(String location) {
        var endpoint = UriComponentsBuilder.fromUriString(location)
                .pathSegment("agent", "v1", "version")
                .toUriString();
        return rest.exchange(endpoint, GET, withContentTypeJson(), VersionInfo.class).getBody();
    }

    @Override
    public long countInserted(String location) {
        var endpoint = UriComponentsBuilder.fromUriString(location)
                .pathSegment("agent", "v1", "results")
                .toUriString();
        var count = rest.exchange(endpoint, GET, withContentTypeJson(), Long.class).getBody();
        return Objects.requireNonNullElse(count, 0L);
    }

    private static String buildEndpoint(String location) {
        return UriComponentsBuilder.fromUriString(location)
                .pathSegment("agent", "v1", "configurations")
                .toUriString();
    }

    private static HttpEntity<?> withContentTypeJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }

}
