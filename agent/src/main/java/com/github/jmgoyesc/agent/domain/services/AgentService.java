package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.config.Configuration;
import com.github.jmgoyesc.agent.domain.models.config.Configuration.ConnectionProperties;
import com.github.jmgoyesc.agent.domain.models.config.DatabaseStatus;
import com.github.jmgoyesc.agent.domain.models.config.TargetDB;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Stream;

import static com.github.jmgoyesc.agent.domain.models.config.Configuration.Status.RUNNING;
import static com.github.jmgoyesc.agent.domain.models.config.Configuration.Status.WAITING_FOR_TERMINATION;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@RequiredArgsConstructor
public class AgentService {

    private final InMemoryStorage storage;
    private final InstanceBuilder builder;

    public Configuration get(String id) {
        return storage.get(id).configuration();
    }

    public DatabaseStatus status(String id) {
        return storage.get(id).instance().getDatabase().status();
    }

    public Collection<Configuration> all() {
        return storage.all();
    }

    public Configuration create(TargetDB target, int vehicles, ConnectionProperties connectionProperties) {
        var configuration = Configuration.builder()
                .target(target)
                .vehicles(vehicles)
                .connection(connectionProperties)
                .build();
        var instance = builder.build(configuration);
        storage.add(configuration, instance);

        return configuration;
    }

    public void start(String id) {
        var pair = storage.get(id);

        pair.instance().getDatabase().createTable();
        pair.instance().getDatabase().cleanData();

        pair.configuration().start();
        pair.instance().startAll();
    }

    public void end(String id) {
        var pair = storage.get(id);

        var stats = pair.instance().stopAll();

        int vehicles = pair.instance().getDatabase().count();
        pair.configuration().stop(vehicles, stats);
    }

    public void delete(String id) {
        var allowToDelete = Stream.of(RUNNING, WAITING_FOR_TERMINATION)
                .noneMatch(status -> status.equals(storage.get(id).configuration().getStatus()));
        if (!allowToDelete) {
            throw new IllegalArgumentException("Configuration can not be deleted while is RUNNING or WAITING_FOR_TERMINATION");
        }

        var pair = storage.get(id);
        pair.instance().getDatabase().dropTable();
        pair.instance().getDatabase().close();
        storage.remove(id);
    }

}
