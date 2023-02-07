package com.github.jmgoyesc.control.domain.services;

import com.github.jmgoyesc.control.domain.models.agents.Agent;
import com.github.jmgoyesc.control.domain.models.agents.Agent.DatasourceType;
import com.github.jmgoyesc.control.domain.models.experiment.ExperimentInfo;
import com.github.jmgoyesc.control.domain.models.experiment.PeriodStatus;
import com.github.jmgoyesc.control.domain.models.experiment.Result;
import com.github.jmgoyesc.control.domain.models.experiment.ResultDuration;
import com.github.jmgoyesc.control.domain.models.experiment.ResultThroughput;
import com.github.jmgoyesc.control.domain.models.experiment.Stats;
import com.github.jmgoyesc.control.domain.models.ports.ExperimentInfoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Juan Manuel Goyes Coral
 */

@Service
@RequiredArgsConstructor
public class ExperimentService {

    private final ExperimentInfoPort port;
    private final CollectorService collector;

    public ExperimentInfo get() {
        var info = port.get();
        if (info.execution().getStatus() == PeriodStatus.on_going) {
            info.execution().setPartialEnd(ZonedDateTime.now());
            collector.collect(info.agents(), this);
            port.update(info);
        }
        return info;
    }

    void addAgents(List<Agent> agents) {
        var info = port.get();

        // reset and clear: set pending status and remove start and end
        info.execution().reset();
        info.results().clear();

        //remove previous agents and add the new ones
        info.agents().clear();
        info.agents().addAll(agents);

        port.update(info);
    }

    void addTablesStart(ZonedDateTime time){
        var info = port.get();
        info.tables().setStart(time);
        port.update(info);
    }
    void addTablesEnd(ZonedDateTime time){
        var info = port.get();
        info.tables().setEnd(time);
        port.update(info);
    }
    void addExecutionStart(ZonedDateTime time){
        var info = port.get();
        info.execution().setStart(time);
        port.update(info);
    }
    void addExecutionEnd(ZonedDateTime time){
        var info = port.get();
        info.execution().setEnd(time);
        port.update(info);
    }

    void addResultsInserted(DatasourceType datasource, Stats stats) {
        var info = port.get();
        var value = calculateStats(info, stats);
        info.results().put(datasource, value);
        port.update(info);
    }

    private Result calculateStats(ExperimentInfo info, Stats stats) {
        long inserted = stats.telemetries() - stats.error();
        var duration = Duration.between(info.execution().getStart(), info.execution().getEnd());

        return Result.builder()
                .inserted(inserted)
                .duration(ResultDuration.builder()
                        .totalInSeconds(duration.toSeconds())
                        .totalInMinutes(duration.toMinutes())
                        .totalInHours(duration.toHours())
                        .build())
                .throughput(ResultThroughput.builder()
                        .rps(calculateThroughput(inserted, duration.toSeconds()))
                        .rpm(calculateThroughput(inserted, duration.toMinutes()))
                        .rph(calculateThroughput(inserted, duration.toHours()))
                        .build())
                .build();
    }

    private double calculateThroughput(long inserted, long time) {
        return (double) inserted / (double) time;
    }
}
