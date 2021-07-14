package org.projetmanager.projetmanager.domain;

import java.nio.file.Path;

public class Project {

    private long id;
    private String name;
    private Path path;
    private Path pom;
    private String groupId;
    private String artifactId;
    private String version;
    private String nameMaven;
    private String groupIdParent;
    private String artifactIdParent;
    private String versionParent;

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

    public Path getPom() {
        return pom;
    }

    public void setPom(Path pom) {
        this.pom = pom;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNameMaven() {
        return nameMaven;
    }

    public void setNameMaven(String nameMaven) {
        this.nameMaven = nameMaven;
    }

    public String getGroupIdParent() {
        return groupIdParent;
    }

    public void setGroupIdParent(String groupIdParent) {
        this.groupIdParent = groupIdParent;
    }

    public String getArtifactIdParent() {
        return artifactIdParent;
    }

    public void setArtifactIdParent(String artifactIdParent) {
        this.artifactIdParent = artifactIdParent;
    }

    public String getVersionParent() {
        return versionParent;
    }

    public void setVersionParent(String versionParent) {
        this.versionParent = versionParent;
    }
}
