package com.github.jmgoyesc.control.domain.models.agents;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import static com.github.jmgoyesc.control.domain.models.agents.AgentResponse.Status.done;
import static com.github.jmgoyesc.control.domain.models.agents.AgentResponse.Status.error;

/**
 * @author Juan Manuel Goyes Coral
 */

@Builder
@Jacksonized
public record AgentResponse(
        String location,
        Status status,
        String message
) {
    public enum Status { done, error }

    public static AgentResponse buildError(String location, String message) {
        return AgentResponse.builder()
                .location(location)
                .status(error)
                .message(message)
                .build();
    }

    public static AgentResponse buildDone(String location) {
        return AgentResponse.builder()
                .location(location)
                .status(done)
                .build();
    }

}
