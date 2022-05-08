package org.projetmanager.projetmanager.properties;

public class CommandProperties {

    private String ifFile;
    private String cmd;

    private boolean terminal;

    public String getIfFile() {
        return ifFile;
    }

    public void setIfFile(String ifFile) {
        this.ifFile = ifFile;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }
}
