package org.projetmanager.projetmanager.utils;

import org.projetmanager.projetmanager.utils.StreamGobbler;

import java.util.List;
import java.util.concurrent.Future;

public class ProcessRun {

    private final long id;
    private final StreamGobbler output;
    private final StreamGobbler error;
    private final Future<?> outputFutur;
    private final Future<?> errorFutur;
    private final Process process;
    private final List<String> cmd;

    public ProcessRun(long id,
               StreamGobbler output, StreamGobbler error,
               Future<?> outputFutur,
               Future<?> errorFutur, Process process,
               List<String> cmd) {
        this.id = id;
        this.output = output;
        this.error = error;
        this.outputFutur = outputFutur;
        this.errorFutur = errorFutur;
        this.process = process;
        this.cmd = List.copyOf(cmd);
    }

    public long getId() {
        return id;
    }

    public List<String> getCmd() {
        return cmd;
    }

    public int waitFor() throws InterruptedException {
        return process.waitFor();
    }

    public boolean isFinished() {
        return !process.isAlive();
    }
}
