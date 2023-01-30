package com.github.jmgoyesc.control.adapters.agent;

import com.github.jmgoyesc.control.domain.models.AgentSignal;

/**
 * @author Juan Manuel Goyes Coral
 */

record Signal(
        AgentSignal.Action signal
) {
}
