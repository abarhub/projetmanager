package org.projetmanager.projetmanager.service;

import org.projetmanager.projetmanager.controller.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MavenService {

    public static final Logger LOGGER = LoggerFactory.getLogger(MavenService.class);

    private String mavenDir;

    private RunService runService;

    public MavenService(String mavenDir, RunService runService) {
        this.mavenDir = mavenDir;
        this.runService=runService;
    }

    public String getDepandences(Path p) throws InterruptedException {
        String s="";
        String mvn="mvn";
        if(StringUtils.hasText(mvn)){
            mvn=mavenDir+"/bin/mvn";
        }
        s=mvn+" dependency:tree";

        List<String> liste=new ArrayList<>();

        List<Boolean> liste2=new ArrayList<>();
        liste2.add(false);
        liste2.add(false);

        Consumer<String> outpout=(x)->{
            LOGGER.info("output:{}",x);
            if(x!=null){
                if(x.startsWith("[INFO] --- maven-dependency-plugin:")){
                    liste2.set(0,true);
                }
                if(liste2.get(0)){
                    if(x.startsWith("[INFO] --------------")){
                        if(!liste2.get(1)) {
                            liste2.set(1, true);
                        } else {
                            liste2.set(0,false);
                            liste2.set(1,false);
                        }
                    }
                }
                if(liste2.get(0)&&!liste2.get(1)){
                    String s2=x;
                    if(s2.startsWith("[INFO]")){
                        s2=s2.substring(6);
                    }
                    liste.add(s2);
                }
            }
        };
        Consumer<String> error=(x)->{
            LOGGER.error("error:{}",x);
        };

        int res=runService.runNow(s,outpout,error,p,false);

        if(res==0){
            return String.join("\n", liste);
        } else {
            return "";
        }
    }

}
