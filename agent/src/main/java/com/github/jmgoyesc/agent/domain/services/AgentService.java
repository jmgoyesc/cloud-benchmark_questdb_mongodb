package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.Configuration;
import com.github.jmgoyesc.agent.domain.models.Configuration.ConnectionProperties;
import com.github.jmgoyesc.agent.domain.models.Configuration.TargetDB;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static com.github.jmgoyesc.agent.domain.models.Configuration.Status.RUNNING;
import static com.github.jmgoyesc.agent.domain.models.Configuration.Status.WAITING_FOR_TERMINATION;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@RequiredArgsConstructor
public class AgentService {

    private final InMemoryStorage storage;

    public Configuration get(String id) {
        //TODO: return connection status
        return storage.get(id);
    }

    public Collection<Configuration> all() {
        return storage.all();
    }

    public Configuration create(TargetDB target, int vehicles, ConnectionProperties connectionProperties) {
        //TDO: disable spring autoconfiguration from mongo and jdbc
        //TODO: received connection parameters to targetDB
        //TODO: open connection to target db -> save connection status into configuration
        //TODO: prepare threads (allocate threads without start)
        var configuration = new Configuration(target, vehicles, connectionProperties);
        storage.add(configuration);
        return configuration;
    }

    public void start(String id) {
        //TODO: change configuration status from CREATED -> RUNNING
        //TODO: start threads: 1 thread per vehicle
        var source = storage.get(id);
        var updated = new Configuration(source, RUNNING);
        storage.update(id, updated);
    }

    public void end(String id) {
        //TODO: change configuration status from RUNNING -> WAITING_FOR_TERMINATION
        //TODO: when all threads finished. then change the configuration status from WAITING_FOR_TERMINATION -> TERMINATED
        //TODO: stop threads
        //TODO: close connection to targetDB
        var source = storage.get(id);
        var updated = new Configuration(source, WAITING_FOR_TERMINATION);
        storage.update(id, updated);
    }

    public void delete(String id) {
        //TODO: do not allow to delete RUNNING or WAITING_FOR_TERMINATION configurations
        storage.remove(id);
    }

}
