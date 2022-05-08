package org.projetmanager.projetmanager.service;


import org.projetmanager.projetmanager.utils.ProcessRun;
import org.projetmanager.projetmanager.utils.StreamGobbler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class RunService {

    public static final Logger LOGGER = LoggerFactory.getLogger(RunService.class);

    private final ExecutorService executorService;

    private final AtomicLong id=new AtomicLong(1);

    public RunService() {
        executorService=Executors.newCachedThreadPool();
    }

    public int runNow(String cmd, Consumer<String> outpout, Consumer<String> error, Path p, boolean terminal) throws InterruptedException {
        ProcessRun processRun=runAsync(cmd,outpout,error,p, terminal);

        if(processRun==null){
            return -1;
        } else {
            return processRun.process().waitFor();
        }
    }

    public ProcessRun runAsync(String cmd, Consumer<String> outpout, Consumer<String> error, Path p, boolean terminal){

        Assert.hasText(cmd,"cmd empty");

        ProcessBuilder processBuilder = new ProcessBuilder();

        if(terminal){
            processBuilder.command(
                    "cmd.exe", "/c",
                    "start","cmd Java",
                    //"/d "+p,
                    "/WAIT",
                    "\""+cmd+"\"");
        } else {
            processBuilder.command("cmd.exe", "/c", cmd);
        }

        try {

            processBuilder.directory(p.toFile());
            Process process = processBuilder.start();
            StreamGobbler streamGobbler =
                    new StreamGobbler(process.getInputStream(), outpout);
            StreamGobbler streamGobbler2 =
                    new StreamGobbler(process.getErrorStream(), error);
            var futureOutput=executorService.submit(streamGobbler);
            var futureError=executorService.submit(streamGobbler2);
            return new ProcessRun(id.getAndIncrement(),streamGobbler,streamGobbler2,
                    futureOutput,futureError,process);

        } catch (IOException e) {
            LOGGER.error("Erreur",e);
        }
        return null;
    }


}
