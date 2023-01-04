package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.config.Configuration;
import com.github.jmgoyesc.agent.domain.models.config.Instance;
import com.github.jmgoyesc.agent.domain.models.ports.DatabasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@RequiredArgsConstructor
public class InstanceBuilder {

    private final ApplicationContext context;

    public Instance build(Configuration configuration) {
        var database = configuration.getTarget().getBuilder().apply(context);
        var actors = actorsBuild(configuration.getVehicles(), database);
        database.createdDS(configuration);

        return new Instance(actors, database);
    }

    private List<Actor> actorsBuild(int vehicles, DatabasePort database) {
        return IntStream.range(0, vehicles)
                .mapToObj(index -> new Actor(index, database))
                .toList();
    }

}
