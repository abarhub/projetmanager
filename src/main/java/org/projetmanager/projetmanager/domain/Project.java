package org.projetmanager.projetmanager.domain;

import java.nio.file.Path;
import java.util.Set;

public class Project {

    private long id;
    private String name;
    private Path path;


    private ProjectMaven projectMaven;

    private ProjectNpm projectNpm;
    private Set<ProjectTypeEnum> projectType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public ProjectMaven getProjectMaven() {
        return projectMaven;
    }

    public void setProjectMaven(ProjectMaven projectMaven) {
        this.projectMaven = projectMaven;
    }

    public ProjectNpm getProjectNpm() {
        return projectNpm;
    }

    public void setProjectNpm(ProjectNpm projectNpm) {
        this.projectNpm = projectNpm;
    }

    public Set<ProjectTypeEnum> getProjectType() {
        return projectType;
    }

    public void setProjectType(Set<ProjectTypeEnum> projectType) {
        this.projectType = projectType;
    }
}
