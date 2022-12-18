package com.github.jmgoyesc.agent.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatterBuilder;

/**
 * @author Juan Manuel Goyes Coral
 */

@Configuration
public class JsonConfig {

    private static void defaults(ObjectMapper mapper) {
        mapper.setPropertyNamingStrategy(new PropertyNamingStrategies.SnakeCaseStrategy())
                .registerModule(timeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    private static JavaTimeModule timeModule() {
        var module = new JavaTimeModule();
        var formatter = new DateTimeFormatterBuilder().appendInstant(3).toFormatter();
        module.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(formatter));
        return module;
    }

    public static ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        defaults(mapper);
        return mapper;
    }

    @Bean
    @Primary
    public ObjectMapper getMapper() {
        return mapper();
    }

}
