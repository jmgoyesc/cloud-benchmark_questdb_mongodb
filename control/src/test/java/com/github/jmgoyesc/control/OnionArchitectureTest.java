package com.github.jmgoyesc.control;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

/**
 * @author Juan Manuel Goyes Coral
 */

@AnalyzeClasses(packages = "com.github.jmgoyesc.agent")
class OnionArchitectureTest {

    @ArchTest
    static final ArchRule onionArchitectureIsRespected = onionArchitecture()
            .domainModels("..domain.models..")
            .domainServices("..domain.services..")
            .applicationServices("..application..", "com.github.jmgoyesc.agent")
            .adapter("mongodb", "..adapters.mongodb..")
            .adapter("questdb", "..adapters.questdb..")
            .adapter("web", "..adapters.web..")
            .allowEmptyShould(true);

}
