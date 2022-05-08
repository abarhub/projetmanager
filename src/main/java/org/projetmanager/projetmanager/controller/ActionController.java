package org.projetmanager.projetmanager.controller;

import org.projetmanager.projetmanager.properties.ScriptProperties;
import org.projetmanager.projetmanager.service.ActionService;
import org.projetmanager.projetmanager.service.RunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Objects;

@RestController
public class ActionController {

    @Autowired
    private ActionService actionService;

    @Autowired
    private ScriptProperties scriptProperties;

    @GetMapping("/run")
    public long run(@RequestParam(value = "action",required = false) String action,
                   @RequestParam(value = "projetId",required = false) Long projetId) throws InterruptedException {
        boolean actionRealise=false;
        if(action!=null&& scriptProperties.getGlobal()!=null&&projetId!=null){

            if(scriptProperties.getGlobal().containsKey(action)){

                return actionService.runAction(action, projetId.longValue());
            }
        }
        if(!actionRealise) {
            actionService.run1();
        }
        return 1;
    }

}
