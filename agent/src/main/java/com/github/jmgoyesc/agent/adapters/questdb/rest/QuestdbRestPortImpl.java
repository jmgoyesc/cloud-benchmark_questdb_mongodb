package com.github.jmgoyesc.agent.adapters.questdb.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jmgoyesc.agent.domain.models.biz.Telemetry;
import com.github.jmgoyesc.agent.domain.models.config.Configuration;
import com.github.jmgoyesc.agent.domain.models.config.DatabaseStatus;
import com.github.jmgoyesc.agent.domain.models.ports.QuestdbRestPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriTemplate;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Scope(SCOPE_PROTOTYPE)
@Log4j2
@RequiredArgsConstructor
class QuestdbRestPortImpl implements QuestdbRestPort {

    private final RestTemplate rest;

    @Override
    public void createdDS(Configuration configuration) {

    }

    @Override
    public void close() {

    }

    @Override
    public DatabaseStatus status() {
        return null;
    }

    @Override
    public void createTable() {

    }

    @Override
    public void dropTable() {

    }

    @Override
    public void cleanData() {

    }

    @Override
    public String insert(Telemetry<?> telemetry) {
        

        return "";
    }

    @Override
    public int count() {
        return 0;
    }

    private void execute(String query) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("query", query);

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        var entity = new HttpEntity<>(body, headers);
        var response = rest.exchange("http://localhost:9000", HttpMethod.GET, entity, JsonNode.class);
    }
}
