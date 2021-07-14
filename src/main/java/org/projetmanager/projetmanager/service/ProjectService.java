package org.projetmanager.projetmanager.service;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.projetmanager.projetmanager.domain.Project;
import org.projetmanager.projetmanager.dto.GitStatusDto;
import org.projetmanager.projetmanager.dto.ProjectDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProjectService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);

    private Path projectsDir;
    private List<Project> projectList = new ArrayList<>();
    private long lastId = 1;
    private MavenService mavenService;
    private GitService gitService;

    public ProjectService(String projectsDir,MavenService mavenService,
                          GitService gitService) {
        Assert.hasText(projectsDir, "projectsDir is empty");
        this.projectsDir = Path.of(projectsDir);
        Assert.isTrue(Files.exists(this.projectsDir), "projectsDir does not exist");
        Assert.isTrue(Files.isDirectory(this.projectsDir), "projectsDir is not a directory");
        this.mavenService=mavenService;
        this.gitService=gitService;
    }

    @PostConstruct
    public void init() throws IOException {
        projectList = new ArrayList<>();
        try (var dirStream = Files.list(projectsDir)) {
            dirStream
                    .filter(path1 -> Files.isDirectory(path1) &&
                            Files.exists(path1.resolve("pom.xml")))
                    .forEach(path -> {

                        Project project = new Project();
                        project.setId(lastId++);
                        project.setName(path.getFileName().toString());
                        project.setPath(path);
                        project.setPom(path.resolve("pom.xml"));
                        fillProject(project);
                        projectList.add(project);
                    });
        }
    }

    private void fillProject(Project project) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new FileReader(project.getPom().toFile(), StandardCharsets.UTF_8)));

            document.getDocumentElement().normalize();
            Element rootElement = document.getDocumentElement();

            NodeList list = document.getElementsByTagName("project");

            if(list!=null&&list.getLength()>0){

                Node node = list.item(0);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    String groupId = getValue(element,"groupId");
                    String artifactId = getValue(element,"artifactId");
                    String version = getValue(element,"version");
                    String name = getValue(element,"name");
                    LOGGER.info("groupId:{}",groupId);
                    LOGGER.info("artifactId:{}",artifactId);
                    LOGGER.info("version:{}",version);
                    project.setGroupId(groupId);
                    project.setArtifactId(artifactId);
                    project.setVersion(version);
                    project.setNameMaven(name);

                    var parent =getElement(element,"parent");
                    if(parent!=null){
                        String groupIdParent = getValue(element,"groupId");
                        String artifactIdParent = getValue(element,"artifactId");
                        String versionParent = getValue(element,"version");
                        project.setGroupIdParent(groupIdParent);
                        project.setArtifactIdParent(artifactIdParent);
                        project.setVersionParent(versionParent);
                    }
                }
            }
        }catch (Exception e){
            LOGGER.error("erreur pour lire le fichier pom du projet:{}",project.getName(),e);
        }
    }

    private Element getElement(Element element,String name){
        NodeList list = element.getElementsByTagName(name);

        if(list!=null&&list.getLength()>0) {

            Node node = list.item(0);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) node;
            }
        }
        return null;
    }

    private String getValue(Element element,String name){
        var elementSelected=getElement(element,name);
        if(elementSelected!=null){
            return elementSelected.getTextContent();
        } else {
            return null;
        }
    }

    public List<ProjectDto> getProjects() {
        var list=new ArrayList<ProjectDto>();
        for(var project:projectList){
            var projectDto=createProjectDto(project);
            getGitInfo(project, projectDto);
            list.add(projectDto);
        }
        return list;
    }

    private ProjectDto createProjectDto(Project project) {
        var projectDto=new ProjectDto();
        projectDto.setId(project.getId());
        projectDto.setName(project.getName());
        projectDto.setPath(project.getPath().toString());
        projectDto.setGroupId(project.getGroupId());
        projectDto.setArtifactId(project.getArtifactId());
        projectDto.setVersion(project.getVersion());
        projectDto.setNameMaven(project.getNameMaven());
        projectDto.setGroupIdParent(project.getGroupIdParent());
        projectDto.setArtifactIdParent(project.getArtifactIdParent());
        projectDto.setVersionParent(project.getVersionParent());
        return projectDto;
    }

    public ProjectDto getDetails(long id){
        var projectOptional=projectList.stream().filter(x->x.getId()==id).findFirst();
        if(projectOptional.isPresent()){
            var project=projectOptional.get();
            var projectDto=createProjectDto(project);
            String dependancy=mavenService.getDepandences(project.getPath());
            projectDto.setDependancy(dependancy);
            getGitInfo(project, projectDto);
            return projectDto;
        } else {
            return null;
        }
    }

    private void getGitInfo(Project project, ProjectDto projectDto) {
        try {
            GitStatusDto status = gitService.getStatus(project.getPath());
            projectDto.setGitStatusDto(status);
        } catch (GitAPIException | IOException e) {
            LOGGER.error("Erreur pour récupérer les informations git du projet : "+ project.getPath(),e);
        }
    }
}
