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
            .domainModels("..domain.models..")
            .domainServices("..domain.services..")
            .applicationServices("..application..", "com.github.jmgoyesc.agent")
            .adapter("mongodb", "..adapters.mongodb..")
            .adapter("questdb", "..adapters.questdb..")
            .adapter("web", "..adapters.web..")
            .ignoreDependency("com.github.jmgoyesc.agent.ApplicationIT__TestContext001_BeanFactoryRegistrations", "com.github.jmgoyesc.agent.adapters.web.controller.AgentController__TestContext001_BeanDefinitions")
            .ignoreDependency("com.github.jmgoyesc.agent.ApplicationIT__TestContext001_BeanFactoryRegistrations", "com.github.jmgoyesc.agent.adapters.web.handler.GlobalExceptionHandler__TestContext001_BeanDefinitions")
            .ignoreDependency("com.github.jmgoyesc.agent.ApplicationIT__TestContext001_BeanFactoryRegistrations", "com.github.jmgoyesc.agent.adapters.mongodb.MongodbPortImpl__TestContext001_BeanDefinitions")
            .ignoreDependency("com.github.jmgoyesc.agent.ApplicationIT__TestContext001_BeanFactoryRegistrations", "com.github.jmgoyesc.agent.adapters.questdb.influx.QuestdbInfluxPortImpl__TestContext001_BeanDefinitions")
            .ignoreDependency("com.github.jmgoyesc.agent.ApplicationIT__TestContext001_BeanFactoryRegistrations", "com.github.jmgoyesc.agent.adapters.questdb.postgres.QuestdbPostgresPortImpl__TestContext001_BeanDefinitions")
            .ignoreDependency("com.github.jmgoyesc.agent.ApplicationIT__TestContext001_BeanFactoryRegistrations", "com.github.jmgoyesc.agent.adapters.questdb.rest.QuestdbRestPortImpl__TestContext001_BeanDefinitions")
            .allowEmptyShould(true);

}
