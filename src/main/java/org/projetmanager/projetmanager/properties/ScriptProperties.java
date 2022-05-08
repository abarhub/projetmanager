package org.projetmanager.projetmanager.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "script")
public class ScriptProperties {

    private Map<String,CommandProperties> global;

    public Map<String, CommandProperties> getGlobal() {
        return global;
    }

    public void setGlobal(Map<String, CommandProperties> global) {
        this.global = global;
    }
}
