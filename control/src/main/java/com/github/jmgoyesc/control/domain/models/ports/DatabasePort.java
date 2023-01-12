package com.github.jmgoyesc.control.domain.models.ports;

import java.util.Optional;

/**
 * @author Juan Manuel Goyes Coral
 */

public interface DatabasePort {
    Optional<String> create(String uri);
}
