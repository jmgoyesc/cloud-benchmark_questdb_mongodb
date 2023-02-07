package com.github.jmgoyesc.agent.adapters.questdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jmgoyesc.agent.domain.models.Telemetry;
import com.github.jmgoyesc.agent.domain.services.ports.QuestdbRestPort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Scope(SCOPE_PROTOTYPE)
@Slf4j
class RestPortImpl implements QuestdbRestPort {

    private static final String SQL_INSERT = """
    INSERT INTO telemetries
         ('received_at', 'originated_at', 'vehicle', 'type', 'source', 'value', 'position')
         VALUES ('%s', '%s', '%s', '%s', '%s', %s, null)
    """;

    private final RestTemplate rest;
    private final String uri;

    public RestPortImpl(String uri, RestTemplateBuilder builder) {
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

        this.rest = builder
                .rootUri(uri)
                .setConnectTimeout(Duration.ofMillis(1000))
                .requestFactory(() -> clientHttpRequestFactory)
                .build();
    }

    @Override
    public boolean insert(Telemetry telemetry) {
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
            return true;
        } catch (RestClientException e) {
            return false;
        }
    }

    @Override
    public void clean() {
    }

    private URI buildEndpoint(String query) {
        return UriComponentsBuilder.fromUriString(this.uri)
                .pathSegment("exec")
                .queryParam("query", query)
                .build()
                .toUri();
    }

}
