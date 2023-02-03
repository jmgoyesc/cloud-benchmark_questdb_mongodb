package com.github.jmgoyesc.control.adapters.questdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jmgoyesc.control.domain.models.ports.QuestdbPort;
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
class QuestdbPortImpl implements QuestdbPort {

    private static final String SQL_DROP = """
    DROP TABLE IF EXISTS telemetries
    """;
    private static final String SQL_CREATE = """
    CREATE TABLE telemetries (
        received_at TIMESTAMP,
        originated_at TIMESTAMP,
        vehicle STRING,
        type SYMBOL,
        source SYMBOL,
        value DOUBLE,
        position geohash(1c)
    ) timestamp(received_at)
    PARTITION BY MONTH
    """;

    private final RestTemplate rest;

    @Override
    public Optional<String> create(String uri) {
        try {
            execute(uri, SQL_DROP);
            execute(uri, SQL_CREATE);
            return Optional.empty();
        } catch (RestClientException e) {
            log.info("[questdb] Exception caught by submitting.", e);
            return Optional.of(StringUtils.defaultString(e.getMessage(), "no message available: %s".formatted(e.getClass())));
        }
    }

    private void execute(String uri, String sql) {
        var query = StringUtils.normalizeSpace(sql);

        var endpoint = UriComponentsBuilder.fromUriString(uri)
                .pathSegment("exec")
                .queryParam("query", query)
                .build()
                .toUri();

        log.info("[questdb] endpoint to be called -> {}", endpoint);
        var response = rest.getForObject(endpoint, JsonNode.class);
        log.info("[questdb] submitted without exception. {}", response);
    }
}
