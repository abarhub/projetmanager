package org.projetmanager.projetmanager.controller;

import org.projetmanager.projetmanager.dto.IndicatorDto;
import org.projetmanager.projetmanager.dto.IndicatorListDto;
import org.projetmanager.projetmanager.dto.TableDto;
import org.projetmanager.projetmanager.service.BitbucketService;
import org.projetmanager.projetmanager.service.IndicatorService;
import org.projetmanager.projetmanager.service.JiraService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController()
public class Main2Controler {

    private final IndicatorService indicatorService;

    private final BitbucketService bitbucketService;

    private final JiraService jiraService;

    public Main2Controler(IndicatorService indicatorService,
                          BitbucketService bitbucketService,
                          JiraService jiraService) {
        this.indicatorService = indicatorService;
        this.bitbucketService = bitbucketService;
        this.jiraService = jiraService;
    }

    @GetMapping("/indicator")
    Mono<IndicatorListDto> listAllIndicators() {
        return indicatorService.listAllIndicators();
    }

    @GetMapping("/indicator/{id}")
    Mono<IndicatorDto> indicator(@PathVariable String id) {
        return indicatorService.getIndicator(id);
    }


    @GetMapping("/bitbucket")
    String testBitbucket() {
        return bitbucketService.test1();
    }

    @GetMapping("/bitbucket/pr")
    TableDto testBitbucketPr() {
        return bitbucketService.getPullRequest();
    }

    @GetMapping("/jira")
    String testJira() {
        return jiraService.getListIssue();
    }

    @GetMapping("/jira/lastVisited")
    TableDto testJira2() {
        return jiraService.getLastVisitedIssue();
    }


}
