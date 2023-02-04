package com.github.jmgoyesc.control.domain.models.experiment;

import lombok.Getter;

import java.time.ZonedDateTime;

import static com.github.jmgoyesc.control.domain.models.experiment.PeriodStatus.completed;
import static com.github.jmgoyesc.control.domain.models.experiment.PeriodStatus.on_going;
import static com.github.jmgoyesc.control.domain.models.experiment.PeriodStatus.pending;

/**
 * @author Juan Manuel Goyes Coral
 */

@Getter
public class Period {
    private PeriodStatus status;
    private ZonedDateTime start;
    private ZonedDateTime end;

    public Period() {
        this.status = pending;
    }

    public void reset() {
        this.status = pending;
        this.start = null;
        this.end = null;
    }

    public void setStart(ZonedDateTime time) {
        this.start = time;
        this.status = on_going;
    }

    public void setEnd(ZonedDateTime time) {
        this.end = time;
        this.status = completed;
    }
}
