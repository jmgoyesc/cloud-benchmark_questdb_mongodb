package com.github.jmgoyesc.agent.adapters.questdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jmgoyesc.agent.domain.models.Telemetry;
import com.github.jmgoyesc.agent.domain.services.ports.QuestdbRestPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

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

    private final RestTemplateBuilder builder;
    private RestTemplate rest;
    private String uri;

    @Override
    public void init(String uri) {
        this.uri = uri;

        var httpClient = HttpClients.custom()
                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                        .setDefaultSocketConfig(SocketConfig.custom()
                                .setSoTimeout(3, TimeUnit.SECONDS)
                                .build())
                        .setMaxConnTotal(2_000)
                        .setMaxConnPerRoute(2_000)
                        .build())
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(3, TimeUnit.SECONDS)
                        .setConnectionRequestTimeout(3, TimeUnit.SECONDS)
                        .build())
                .build();

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        this.rest = this.builder
                .rootUri(uri)
                .setConnectTimeout(Duration.ofMillis(1000))
                .requestFactory(() -> clientHttpRequestFactory)
                .build();
    }

    @Override
    public void insert(Telemetry telemetry) {
        var query = StringUtils.normalizeSpace(SQL_INSERT)
                .formatted(
                        telemetry.receivedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")),
                        telemetry.originatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")),
                        telemetry.vehicle(),
                        telemetry.type(),
                        telemetry.source().name(),
                        telemetry.value());

        var endpoint = buildEndpoint(query);

        try {
            rest.getForObject(endpoint, JsonNode.class);
            log.info("{} telemetry inserted", LOG_PREFIX);
        } catch (RestClientException e) {
            log.error("{} exception inserting telemetry", LOG_PREFIX, e);
        }
    }

    @Override
    public long count() {
        var query = StringUtils.normalizeSpace(SQL_COUNT);
        var endpoint = buildEndpoint(query);

        try {
            var response = rest.getForObject(endpoint, JsonNode.class);
            if (response == null || !response.hasNonNull("dataset") || response.get("dataset").get(0) == null || response.get("dataset").get(0).get(0) == null) {
                log.error("{} exception counting telemetry. Invalid count response {}", LOG_PREFIX, response);
                return 0L;
            }
            return response.get("dataset").get(0).get(0).asLong();
        } catch (RestClientException e) {
            log.error("{} exception counting telemetry", LOG_PREFIX, e);
            return 0L;
        }
    }

    private URI buildEndpoint(String query) {
        return UriComponentsBuilder.fromUriString(this.uri)
                .pathSegment("exec")
                .queryParam("query", query)
                .build()
                .toUri();
    }

}
