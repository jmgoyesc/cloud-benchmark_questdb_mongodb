package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.config.Configuration;
import com.github.jmgoyesc.agent.domain.models.config.Instance;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
class InMemoryStorage {

    private final Map<String, Configuration> storage = new ConcurrentHashMap<>();
    private final Map<String, Instance> instances = new ConcurrentHashMap<>();

    public record Pair(Configuration configuration, Instance instance) {}

    void add(Configuration configuration, Instance instance) {
        var id = configuration.getId();
        storage.put(id, configuration);
        instances.put(id, instance);
    }

    void remove(String id) {
        var removed = storage.remove(id);
        instances.remove(id);
        if (removed == null)
            throwNotFound(id);
    }

    Pair get(String id) {
        var found = storage.get(id);
        if (found == null)
            throwNotFound(id);

        var instance = instances.get(id);
        return new Pair(found, instance);
    }

    Collection<Configuration> all() {
        return storage.values();
    }

    private static void throwNotFound(String id) {
        throw new IllegalArgumentException("Configuration (id=%s) does not exits".formatted(id));
    }

}
