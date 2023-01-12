package com.github.jmgoyesc.control.adapters.questdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jmgoyesc.control.domain.models.ports.QuestdbPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
class QuestdbPortImpl implements QuestdbPort {

    public static final String SQL_CREATE = """
    CREATE TABLE IF NOT EXISTS telemetries (
        received_at TIMESTAMP,
        originated_at TIMESTAMP,
        vehicle STRING,
        value DOUBLE,
        position geohash(1c)
    ) timestamp(received_at)
    """;

    private final RestTemplate rest;

    @Override
    public Optional<String> create(String uri) {
        var query = StringUtils.normalizeSpace(SQL_CREATE);

        var endpoint = UriComponentsBuilder.fromUriString(uri)
                .pathSegment("exec")
                .queryParam("query", query)
                .build()
                .toUri();

        try {
            var response = rest.getForObject(endpoint, JsonNode.class);
            log.info("[questdb] submitted without exception. {}", response);
            return Optional.empty();
        } catch (RestClientException e) {
            log.info("[questdb] Exception caught by submitting.", e);
            return Optional.of(StringUtils.defaultString(e.getMessage(), "no message available: %s".formatted(e.getClass())));
        }
    }
}
