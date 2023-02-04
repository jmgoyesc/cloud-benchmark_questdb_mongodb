package com.github.jmgoyesc.agent.adapters.questdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jmgoyesc.agent.domain.models.Telemetry;
import com.github.jmgoyesc.agent.domain.services.ports.QuestdbRestPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.format.DateTimeFormatter;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Slf4j
@RequiredArgsConstructor
class RestPortImpl implements QuestdbRestPort {

    private static final String LOG_PREFIX = "[questdb - rest]";
    private static final String SQL_INSERT = """
    INSERT INTO telemetries
         ('received_at', 'originated_at', 'vehicle', 'type', 'source', 'value', 'position')
         VALUES ('%s', '%s', '%s', '%s', '%s', %s, null)
    """;
    private static final String SQL_COUNT = """
    SELECT count() from telemetries
    """;

    private final RestTemplate rest;

    //TODO: pre initialize rest template (faster?)
    @Override
    public void insert(String uri, Telemetry telemetry) {
        var query = StringUtils.normalizeSpace(SQL_INSERT)
                .formatted(
                        telemetry.receivedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")),
                        telemetry.originatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")),
                        telemetry.vehicle(),
                        telemetry.type(),
                        telemetry.source().name(),
                        telemetry.value());

        var endpoint = buildEndpoint(uri, query);

        try {
            var response = rest.getForObject(endpoint, JsonNode.class);
            log.info("{} submitted without exception. response: {}, telemetry: {}", LOG_PREFIX, response, telemetry);
        } catch (RestClientException e) {
            log.info("{} Exception caught by submitting. telemetry: {}", LOG_PREFIX, telemetry, e);
        }
    }

    @Override
    public long count(String uri) {
        var query = StringUtils.normalizeSpace(SQL_COUNT);
        var endpoint = buildEndpoint(uri, query);

        try {
            var response = rest.getForObject(endpoint, JsonNode.class);
            if (response == null || !response.hasNonNull("dataset") || response.get("dataset").get(0) == null || response.get("dataset").get(0).get(0) == null) {
                log.info("{} Invalid response after counting. response: {}", LOG_PREFIX, response);
                return 0L;
            }
            return response.get("dataset").get(0).get(0).asLong();
        } catch (RestClientException e) {
            log.info("{} Exception caught by counting inserted.", LOG_PREFIX, e);
            return 0L;
        }
    }

    private URI buildEndpoint(String uri, String query) {
        return UriComponentsBuilder.fromUriString(uri)
                .pathSegment("exec")
                .queryParam("query", query)
                .build()
                .toUri();
    }

}
