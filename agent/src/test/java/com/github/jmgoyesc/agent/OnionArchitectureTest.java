package com.github.jmgoyesc.agent;

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
            .domainModels("..domain.model..")
            .domainServices("..domain.service..")
            .applicationServices("..application..")
            .adapter("mongodb", "..adapter.mongodb..")
            .adapter("questdb", "..adapter.questdb..")
            .adapter("web", "..adapter.web..")
            .allowEmptyShould(true);

}
