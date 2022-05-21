package org.projetmanager.projetmanager.service;

import org.projetmanager.projetmanager.dto.ProcesRunDto;
import org.projetmanager.projetmanager.properties.ScriptProperties;
import org.projetmanager.projetmanager.utils.ProcessRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ActionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionService.class);

    private ScriptProperties scriptProperties;

    private ProjectService projectService;

    private RunService runService;

    private Map<Long, ProcessRun> processRunMap;

    public ActionService(ScriptProperties scriptProperties, ProjectService projectService, RunService runService) {
        this.scriptProperties = scriptProperties;
        this.projectService = projectService;
        this.runService = runService;
        processRunMap = new ConcurrentHashMap<>();
    }

    public long runAction(String action, long projetId) throws InterruptedException {
        Assert.hasText(action, "Action empty");
        var cmd = scriptProperties.getGlobal().get(action);
        Assert.notNull(cmd, "Action not found");
        Assert.hasText(cmd.getCmd(), "cmd empty for action " + action);
        var project = projectService.getProjet(projetId).orElseThrow(() -> new IllegalArgumentException("Projet " + projetId + " inexistant"));
        LOGGER.info("runAction '{}' projet '{}' (path={})", action, projetId, project.getPath());
        var res = runService.runAsync(cmd.getCmd(), (x) -> LOGGER.info("out: {}", x),
                (x) -> LOGGER.error("err:{}", x), project.getPath(), cmd.isTerminal(),
                project.getName());
        LOGGER.info("fin runAction: {}", res);
        processRunMap.put(res.id(), res);
        return res.id();
    }

    public void run1() throws InterruptedException {
        LOGGER.info("run1");
        var res = runService.runNow("echo test", (x) -> LOGGER.info("out: {}", x), (x) -> LOGGER.error("err:{}", x), Path.of("."), false);
        LOGGER.info("fin run1: {}", res);
    }

    public List<ProcesRunDto> getProcessRunListDto(){
        var list=new ArrayList<ProcesRunDto>();
        for(var entry:processRunMap.entrySet()){
            var process=entry.getValue();
            var cmd=process.cmd().stream().collect(Collectors.joining(""));
            ProcesRunDto procesRunDto=new ProcesRunDto(entry.getKey(),cmd,true);
            list.add(procesRunDto);
        }
        return list;
    }

}
