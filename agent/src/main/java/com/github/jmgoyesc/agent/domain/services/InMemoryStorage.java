package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.Configuration;
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

    void add(Configuration configuration) {
        var id = configuration.id();
        storage.put(id, configuration);
    }

    void update(String id, Configuration configuration) {
        storage.replace(id, configuration);
    }

    void remove(String id) {
        var removed = storage.remove(id);
        if (removed == null)
            throwNotFound(id);
    }

    Configuration get(String id) {
        var found = storage.get(id);
        if (found == null)
            throwNotFound(id);
        return found;
    }

    Collection<Configuration> all() {
        return storage.values();
    }

    private static void throwNotFound(String id) {
        throw new IllegalArgumentException("Configuration (id=%s) does not exits".formatted(id));
    }

}
