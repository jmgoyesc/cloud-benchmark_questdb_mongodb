package com.github.jmgoyesc.agent.application;

import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;

import java.util.concurrent.Executors;

/**
 * @author Juan Manuel Goyes Coral
 */

@Configuration
public class VirtualThreadsConfig {

//    @Bean
//    public AsyncTaskExecutor appTaskExecutor() {
//        var executor = Executors.newVirtualThreadPerTaskExecutor();
//        return new TaskExecutorAdapter(executor);
//    }

//    @Bean
//    public TomcatProtocolHandlerCustomizer<?> protocolHandlerCustomizer() {
//        return handler -> handler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
//    }

}
