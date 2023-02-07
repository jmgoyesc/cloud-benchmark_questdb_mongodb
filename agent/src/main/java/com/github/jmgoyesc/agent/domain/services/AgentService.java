package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.Config;
import com.github.jmgoyesc.agent.domain.models.Stats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class AgentService {

    private final DatabasePortCreator creator;

    private Config config;
    private List<LoadGenerator> workers = List.of();
    private ExecutorService executor;
    private MonitorService monitor;

    public void configure(Config config) {
        this.config = config;
        this.workers = IntStream.rangeClosed(1, config.vehicles())
                .mapToObj(it -> new LoadGenerator(creator.newInstance(config), config.db(), "vehicle-" + it))
                .toList();
        this.executor = Executors.newFixedThreadPool(config.vehicles());
    }

    public void start() {
        log.info("[start] Signal received to start the process");
        if (config == null) {
            throw new RuntimeException("Configure was not called for agent. Pending configuration: POST /v1/configurations");
        }
        LoadGenerator.running = true;
        this.workers.forEach(executor::submit);
        triggerMonitor();
    }

    public void stop() {
        LoadGenerator.running = false;
        if (executor != null) {
            executor.shutdown();
            executor.close();
        }
        cancelMonitor();
        log.info("[stop] Signal 'stop' completed. workers: {}", workers.size());
    }

    public Stats getResults() {
        if (monitor == null)
            return new Stats(0L, 0L);
        return monitor.getResults();
    }

    private void triggerMonitor() {
        monitor = new MonitorService(this.workers);
    }

    private void cancelMonitor() {
        if (monitor != null)
            monitor.cancel();
    }

}
