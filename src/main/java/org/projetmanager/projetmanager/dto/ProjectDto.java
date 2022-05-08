package org.projetmanager.projetmanager.dto;

import java.util.ArrayList;
import java.util.List;

public class ProjectDto {

    private long id;
    private String name;
    private String path;
    private String groupId;
    private String artifactId;
    private String version;
    private String nameMaven;
    private String groupIdParent;
    private String artifactIdParent;
    private String versionParent;
    private String dependancy;
    private GitStatusDto gitStatusDto;

    private List<ActionDto> listeActions;

    private String nomNpm;

    private String versionNpm;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public String getArtifactVersion(){
        if(groupId==null&&artifactId==null&&version==null){
            return "";
        } else{
            return groupId+":"+artifactId+":"+version;
        }
    }

    public String getArtifactParentVersion(){
        if(groupIdParent==null&&artifactIdParent==null&&versionParent==null){
            return "";
        } else{
            return groupIdParent+":"+artifactIdParent+":"+versionParent;
        }
    }

    public String getDependancy() {
        return dependancy;
    }

    public void setDependancy(String dependancy) {
        this.dependancy = dependancy;
    }

    public GitStatusDto getGitStatusDto() {
        return gitStatusDto;
    }

    public void setGitStatusDto(GitStatusDto gitStatusDto) {
        this.gitStatusDto = gitStatusDto;
    }

    public List<ActionDto> getListeActions() {
        return listeActions;
    }

    public void setListeActions(List<ActionDto> listeActions) {
        this.listeActions = listeActions;
    }

    public void addActions(ActionDto actionDto){
        if(listeActions==null){
            listeActions=new ArrayList<>();
        }
        listeActions.add(actionDto);
    }

    public String getNomNpm() {
        return nomNpm;
    }

    public void setNomNpm(String nomNpm) {
        this.nomNpm = nomNpm;
    }

    public String getVersionNpm() {
        return versionNpm;
    }

    public void setVersionNpm(String versionNpm) {
        this.versionNpm = versionNpm;
    }
}
