package org.projetmanager.projetmanager.service;


import org.projetmanager.projetmanager.utils.ProcessRun;
import org.projetmanager.projetmanager.utils.StrUtils;
import org.projetmanager.projetmanager.utils.StreamGobbler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class RunService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunService.class);

    private final ExecutorService executorService;

    private final AtomicLong id = new AtomicLong(1);

    public RunService() {
        executorService = Executors.newCachedThreadPool();
    }

    public int runNow(String cmd, Consumer<String> outpout, Consumer<String> error, Path p, boolean terminal) throws InterruptedException {
        ProcessRun processRun = runAsync(cmd, outpout, error, p, terminal, "");

        if (processRun == null) {
            return -1;
        } else {
            return processRun.waitFor();
        }
    }

    public ProcessRun runAsync(String cmd, Consumer<String> outpout, Consumer<String> error,
                               Path p, boolean terminal, String name) {

        Assert.hasText(cmd, "cmd is empty");

        ProcessBuilder processBuilder = new ProcessBuilder();
        List<String> list = new ArrayList<>();
        if (terminal) {
            var cmdBegin = List.of("cmd.exe", "/c",
                    "start",
                    "cmd Java" + ((StringUtils.hasText(name)) ? " " + name : ""),
                    "/WAIT");
            var cmdList = StrUtils.splitString(cmd);

            list.addAll(cmdBegin);
            list.addAll(cmdList);
            LOGGER.info("Run (with terminal): {}", list);
            processBuilder.command(list);
        } else {
            LOGGER.info("Run: {}", cmd);
            list.add("cmd.exe");
            list.add("/c");
            list.add(cmd);
            processBuilder.command(list);
        }

        try {

            addEnv(processBuilder, name);
            processBuilder.directory(p.toFile());
            Process process = processBuilder.start();
            StreamGobbler streamGobbler =
                    new StreamGobbler(process.getInputStream(), outpout, false);
            StreamGobbler streamGobbler2 =
                    new StreamGobbler(process.getErrorStream(), error, true);
            var futureOutput = executorService.submit(streamGobbler);
            var futureError = executorService.submit(streamGobbler2);
            return new ProcessRun(id.getAndIncrement(), streamGobbler, streamGobbler2,
                    futureOutput, futureError, process, list);

        } catch (IOException e) {
            LOGGER.error("Erreur", e);
        }
        return null;
    }

    private void addEnv(ProcessBuilder processBuilder, String name) {
        var env = processBuilder.environment();
        if (StringUtils.hasText(name)) {
            env.put("APP", name);
        } else {
            env.put("APP", "Application");
        }
    }


}
