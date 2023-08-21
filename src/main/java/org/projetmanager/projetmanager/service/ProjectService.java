package org.projetmanager.projetmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.projetmanager.projetmanager.domain.Project;
import org.projetmanager.projetmanager.domain.ProjectMaven;
import org.projetmanager.projetmanager.domain.ProjectNpm;
import org.projetmanager.projetmanager.domain.ProjectTypeEnum;
import org.projetmanager.projetmanager.dto.ActionDto;
import org.projetmanager.projetmanager.dto.GitStatusDto;
import org.projetmanager.projetmanager.dto.ProjectDto;
import org.projetmanager.projetmanager.properties.ScriptProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
import java.util.*;

public class ProjectService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);

    private Path projectsDir;
    private List<Project> projectList = new ArrayList<>();
    private long lastId = 1;
    private MavenService mavenService;
    private GitService gitService;

    private ScriptProperties scriptProperties;

    public ProjectService(String projectsDir,MavenService mavenService,
                          GitService gitService, ScriptProperties scriptProperties) {
        Assert.hasText(projectsDir, "projectsDir is empty");
        this.projectsDir = Path.of(projectsDir);
        Assert.isTrue(Files.exists(this.projectsDir), "projectsDir does not exist");
        Assert.isTrue(Files.isDirectory(this.projectsDir), "projectsDir is not a directory");
        this.mavenService=mavenService;
        this.gitService=gitService;
        this.scriptProperties=scriptProperties;
    }

    @PostConstruct
    public void init() throws IOException {
        projectList = new ArrayList<>();
        try (var dirStream = Files.list(projectsDir)) {
            dirStream
                    .filter(path1 -> Files.isDirectory(path1) &&
                            (Files.exists(path1.resolve("pom.xml"))
                            ||Files.exists(path1.resolve("package.json"))))
                    .forEach(path -> {

                        Project project = new Project();
                        project.setId(lastId++);
                        project.setName(path.getFileName().toString());
                        project.setPath(path);
                        if(Files.exists(path.resolve("pom.xml"))) {
                            project.setProjectMaven(buildMaven(path.resolve("pom.xml")));
                            //project.setPom(path.resolve("pom.xml"));
                            project.setProjectType(Set.of(ProjectTypeEnum.MAVEN));
                        }
                        if(Files.exists(path.resolve("package.json"))){
                            project.setProjectNpm(buildNpm(path.resolve("package.json")));
                            if(project.getProjectType()==null){
                                project.setProjectType(Set.of(ProjectTypeEnum.NPM));
                            } else {
                                Set<ProjectTypeEnum> set=new HashSet<>(project.getProjectType());
                                set.add(ProjectTypeEnum.NPM);
                                project.setProjectType(Set.copyOf(set));
                            }
                        }
                        projectList.add(project);
                    });
        }
    }


    private ProjectNpm buildNpm(Path file) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                var json=mapper.readTree(Files.newBufferedReader(file));
                if(json!=null){
                    String nomNpm=null;
                    String versionNpm=null;
                    List<String> listCommands=List.of();
                    var nameNode=json.get("name");
                    if(nameNode!=null){
                        var nom=nameNode.asText();
                        if(StringUtils.hasText(nom)){
                            nomNpm=nom;
                        }
                    }
                    var versionNode=json.get("version");
                    if(versionNode!=null){
                        var version=versionNode.asText();
                        if(StringUtils.hasText(version)){
                            versionNpm=version;
                        }
                    }
                    var scriptNode=json.get("scripts");
                    if(scriptNode!=null){
                        var iter=scriptNode.fieldNames();
                        if(iter!=null){
                            List<String> list=new ArrayList<>();
                            while (iter.hasNext()){
                                var nom=iter.next();
                                if(StringUtils.hasText(nom)){
                                    list.add(nom);
                                }
                            }
                            if(!list.isEmpty()){
                                listCommands=List.copyOf(list);
                            }
                        }
                    }
                    return new ProjectNpm(file,nomNpm,versionNpm, listCommands);
                }
            }catch(Exception e){
                LOGGER.error("Erreur", e);
            }
            return null;
    }

    private ProjectMaven buildMaven(Path file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new FileReader(file.toFile(), StandardCharsets.UTF_8)));

            document.getDocumentElement().normalize();
            Element rootElement = document.getDocumentElement();

            NodeList list = document.getElementsByTagName("project");

            if (list != null && list.getLength() > 0) {

                Node node = list.item(0);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    String groupId = getValue(element, "groupId");
                    String artifactId = getValue(element, "artifactId");
                    String version = getValue(element, "version");
                    String name = getValue(element, "name");
                    String groupIdParent = null;
                    String artifactIdParent = null;
                    String versionParent = null;
                    LOGGER.info("groupId:{}", groupId);
                    LOGGER.info("artifactId:{}", artifactId);
                    LOGGER.info("version:{}", version);

                    var parent = getElement(element, "parent");
                    if (parent != null) {
                        groupIdParent = getValue(element, "groupId");
                        artifactIdParent = getValue(element, "artifactId");
                        versionParent = getValue(element, "version");
                    }
                    ProjectMaven projectMaven=new ProjectMaven(file,name,version,
                            groupId,artifactId,
                            groupIdParent,artifactIdParent,versionParent);
                    return projectMaven;
                }
            }
        } catch (Exception e) {
            LOGGER.error("erreur pour lire le fichier pom du projet:{}", file, e);
        }
        return null;
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
            addActions(projectDto);
            list.add(projectDto);
        }
        return list;
    }

    private void addActions(ProjectDto projectDto) {
        if(StringUtils.hasText(projectDto.getPath())) {
            Path path = Path.of(projectDto.getPath());
            if(scriptProperties.getGlobal()!=null) {
                for (var entry : scriptProperties.getGlobal().entrySet()) {
                    var code=entry.getKey();
                    var value=entry.getValue();
                    if(StringUtils.hasText(value.getIfFile())) {
                        if (Files.exists(path.resolve(value.getIfFile()))) {
                            addActionDto(projectDto, code);
                        }
                    } else {
                        addActionDto(projectDto, code);
                    }
                }
            }
        }
    }

    private void addActionDto(ProjectDto projectDto, String code) {
        projectDto.addActions(new ActionDto("Action "+ code, code));
    }

    private ProjectDto createProjectDto(Project project) {
        var projectDto=new ProjectDto();
        projectDto.setId(project.getId());
        projectDto.setName(project.getName());
        projectDto.setPath(project.getPath().toString());
        if(project.getProjectMaven()!=null) {
            projectDto.setGroupId(project.getProjectMaven().groupId());
            projectDto.setArtifactId(project.getProjectMaven().artifactId());
            projectDto.setVersion(project.getProjectMaven().version());
            projectDto.setNameMaven(project.getProjectMaven().name());
            projectDto.setGroupIdParent(project.getProjectMaven().groupIdParent());
            projectDto.setArtifactIdParent(project.getProjectMaven().artifactIdParent());
            projectDto.setVersionParent(project.getProjectMaven().versionParent());
        }
        if(project.getProjectNpm()!=null) {
            projectDto.setNomNpm(project.getProjectNpm().name());
            projectDto.setVersionNpm(project.getProjectNpm().version());
            if(project.getProjectNpm().commandes()!=null){
                for(String cmd:project.getProjectNpm().commandes()){
                    projectDto.addActions(new ActionDto("Action NPM "+ cmd, cmd));
                }
            }
        }
        return projectDto;
    }

    public ProjectDto getDetails(long id) throws InterruptedException {
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

    public Optional<Project> getProjet(long id){
        return projectList.stream().filter(x->x.getId()==id).findFirst();
    }
}
