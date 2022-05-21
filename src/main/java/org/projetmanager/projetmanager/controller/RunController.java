package org.projetmanager.projetmanager.controller;

import org.projetmanager.projetmanager.service.ActionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller
public class RunController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunController.class);

    @Autowired
    private ActionService actionService;

    @RequestMapping(value = {"/listRun"}, method = RequestMethod.GET)
    public String index(Model model) throws IOException {
        LOGGER.info("index");

        model.addAttribute("message", "aaa");

        var list = actionService.getProcessRunListDto();
        model.addAttribute("processList", list);

        return "listRun";
    }
}
