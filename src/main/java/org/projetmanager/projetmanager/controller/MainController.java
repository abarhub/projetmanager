package org.projetmanager.projetmanager.controller;

import org.projetmanager.projetmanager.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller
public class MainController {

    public static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private ProjectService projectService;

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String index(Model model) throws IOException {
        LOGGER.info("index");

        model.addAttribute("message", "aaa");

        var list = projectService.getProjects();
        model.addAttribute("projectList", list);

        return "index";
    }

    @RequestMapping(value = {"/getmaven"}, method = RequestMethod.GET)
    public String getmaven(Model model) {
        LOGGER.info("getmaven");

        model.addAttribute("message", "aaa");

        return "index";
    }

    @RequestMapping(value = "/details/{id}", method = RequestMethod.GET)
    public String details(@PathVariable("id") long id, Model model) throws InterruptedException {
        LOGGER.info("details");
        var projet = projectService.getDetails(id);
        model.addAttribute("projet", projet);
        return "details";
    }

}
