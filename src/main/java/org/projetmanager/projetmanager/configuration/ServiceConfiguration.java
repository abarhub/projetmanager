package org.projetmanager.projetmanager.configuration;

import org.projetmanager.projetmanager.service.GitService;
import org.projetmanager.projetmanager.service.MavenService;
import org.projetmanager.projetmanager.service.ProjectService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

    @Bean
    public MavenService mavenService(@Value("${maven.dir}") String mavenDir){
        return new MavenService(mavenDir);
    }

    @Bean
    public ProjectService projectService(@Value("${projets.dir}") String projectsDir,
                                         MavenService mavenService,
                                         GitService gitService){
        return new ProjectService(projectsDir, mavenService, gitService);
    }

    @Bean
    public GitService gitService(){
        return new GitService();
    }
}
