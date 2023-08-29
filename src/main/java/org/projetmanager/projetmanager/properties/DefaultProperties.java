package org.projetmanager.projetmanager.properties;

public class DefaultProperties {

    private int size;
    private String minWidth;
    private String maxWidth;

    private DefaultJiraProperties jira;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(String minWidth) {
        this.minWidth = minWidth;
    }

    public String getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(String maxWidth) {
        this.maxWidth = maxWidth;
    }

    public DefaultJiraProperties getJira() {
        return jira;
    }

    public void setJira(DefaultJiraProperties jira) {
        this.jira = jira;
    }
}
