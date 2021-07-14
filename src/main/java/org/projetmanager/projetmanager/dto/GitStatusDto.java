package org.projetmanager.projetmanager.dto;

import java.time.LocalDateTime;
import java.util.List;

public class GitStatusDto {

    private String lastCommit;
    private LocalDateTime dateTimeLastCommit;
    private List<String> branches;
    private boolean clean;
    private String auteur;

    public String getLastCommit() {
        return lastCommit;
    }

    public void setLastCommit(String lastCommit) {
        this.lastCommit = lastCommit;
    }

    public LocalDateTime getDateTimeLastCommit() {
        return dateTimeLastCommit;
    }

    public void setDateTimeLastCommit(LocalDateTime dateTimeLastCommit) {
        this.dateTimeLastCommit = dateTimeLastCommit;
    }

    public List<String> getBranches() {
        return branches;
    }

    public void setBranches(List<String> branches) {
        this.branches = branches;
    }

    public boolean isClean() {
        return clean;
    }

    public void setClean(boolean clean) {
        this.clean = clean;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }
}
