package org.projetmanager.projetmanager.model;

public enum TypeIndicator {
    REST("rest"), SPRING_BOOT_ACTUATOR("springBootActuator"), JIRA("jira");

    TypeIndicator(String value) {
        this.value = value;
    }

    private final String value;

    public String getValue() {
        return value;
    }
}
