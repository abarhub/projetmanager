package org.projetmanager.projetmanager.configuration;

import org.projetmanager.projetmanager.properties.ScriptProperties;
import org.projetmanager.projetmanager.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ScriptProperties.class)
public class ServiceConfiguration {

    @Bean
    public MavenService mavenService(@Value("${maven.dir}") String mavenDir,
                                     RunService runService) {
        return new MavenService(mavenDir, runService);
    }

    @Bean
    public ProjectService projectService(@Value("${projets.dir}") String projectsDir,
                                         MavenService mavenService,
                                         GitService gitService,
                                         ScriptProperties scriptProperties) {
        return new ProjectService(projectsDir, mavenService, gitService, scriptProperties);
    }

    @Bean
    public GitService gitService() {
        return new GitService();
    }

    @Bean
    public RunService runService() {
        return new RunService();
    }

    @Bean
    public ActionService actionService(ScriptProperties scriptProperties,
                                       ProjectService projectService, RunService runService){
        return new ActionService(scriptProperties,projectService,runService );
    }
}
