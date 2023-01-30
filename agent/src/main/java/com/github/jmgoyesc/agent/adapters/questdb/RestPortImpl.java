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

import java.time.format.DateTimeFormatter;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Slf4j
@RequiredArgsConstructor
class RestPortImpl implements QuestdbRestPort {

    private static final String SQL_INSERT = """
    INSERT INTO telemetries VALUES ('%s', '%s', '%s', '%s', %s, null)
    """;

    private final RestTemplate rest;

    @Override
    public void insert(String uri, Telemetry telemetry) {
        var query = StringUtils.normalizeSpace(SQL_INSERT)
                .formatted(
                        telemetry.receivedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")),
                        telemetry.originatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")),
                        "rest-" + telemetry.vehicle(),
                        telemetry.type(),
                        telemetry.value());

        var endpoint = UriComponentsBuilder.fromUriString(uri)
                .pathSegment("exec")
                .queryParam("query", query)
                .build()
                .toUri();

        try {
            var response = rest.getForObject(endpoint, JsonNode.class);
            log.info("[questdb] submitted without exception. {}", response);
        } catch (RestClientException e) {
            log.info("[questdb] Exception caught by submitting.", e);
        }
    }


}
