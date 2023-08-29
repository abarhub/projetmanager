package org.projetmanager.projetmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.tomcat.util.codec.binary.Base64;
import org.projetmanager.projetmanager.dto.IndicatorDto;
import org.projetmanager.projetmanager.dto.TableDto;
import org.projetmanager.projetmanager.model.Indicator;
import org.projetmanager.projetmanager.properties.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JiraService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JiraService.class);

    private final String username;

    private final String password;

    private final String url;

    private final String urlRoot;

    private final AppProperties appProperties;

    private Optional<WebClient> webClient = Optional.empty();

    public JiraService(String username, String password, String url, String urlRoot, AppProperties appProperties) {
        this.username = username;
        this.password = password;
        this.url = url;
        this.urlRoot = urlRoot;
        this.appProperties = appProperties;
    }

    public String getListIssue() {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        var headers = createHeaders(username, password);
        headers.add("Content-Type", MediaType.APPLICATION_JSON.toString());
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();

        var httpEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);


        LOGGER.info("response={}", response);
        LOGGER.info("body={}", response.getBody());

        return "OK";
    }

    public TableDto getLastVisitedIssue() {

        TableDto tableDto = new TableDto();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        var headers = createHeaders(username, password);
        headers.add("Content-Type", MediaType.APPLICATION_JSON.toString());
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();

        var httpEntity = new HttpEntity<>(requestBody, headers);

        var url = urlRoot + "//search?jql={query}";

        Map<String, String> params = new HashMap<String, String>();
        params.put("query", "issuekey in issueHistory() ORDER BY lastViewed DESC");


        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.GET, httpEntity, String.class, params);


        LOGGER.info("response={}", response);
        LOGGER.info("body={}", response.getBody());

        if (response.hasBody()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                var listIssue = root.get("issues");
                if (listIssue != null && listIssue.isArray()) {
                    for (var i = 0; i < listIssue.size(); i++) {
                        if (tableDto.getHeaders() == null) {
                            tableDto.setHeaders(new ArrayList<>());
                            tableDto.getHeaders().add("key");
                            tableDto.getHeaders().add("url");
                            tableDto.getHeaders().add("lastUpdate");
                            tableDto.getHeaders().add("statut");
                            tableDto.getHeaders().add("summary");
                            tableDto.getHeaders().add("description");
                        }
                        var line = new ArrayList<String>();
                        if (tableDto.getRows() == null) {
                            tableDto.setRows(new ArrayList<>());
                        }
                        tableDto.getRows().add(line);
                        var elt = listIssue.get(i);
                        var key = toString(elt.get("key"));
                        var urlIssue = toString(elt.get("self"));
                        var lastUpdate = "";
                        var statut = "";
                        var summary = "";
                        var description = "";
                        if (elt.has("fields")) {
                            var fields = elt.get("fields");
                            lastUpdate = toString(fields.get("updated"));
                            if (fields.get("status") != null) {
                                statut = toString(fields.get("status").get("name"));
                            }
                            summary = toString(fields.get("summary"));
                            description = toString(fields.get("description"));
                        }
                        line.add(key);
                        line.add(urlIssue);
                        line.add(lastUpdate);
                        line.add(statut);
                        line.add(summary);
                        line.add(description);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Erreur pour lire le flux", e);
            }
        }

        return tableDto;
    }

    private String toString(JsonNode node) {
        if (node == null) {
            return "";
        } else {
            return node.asText();
        }
    }

    private HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(StandardCharsets.US_ASCII),false);
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
        }};
    }

    public Mono<IndicatorDto> getLastVisitedIssue(Indicator item) {
        return getWebClient().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("jql", "{query}")
                        .build(item.getProperties().get("query")))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap((body) -> convertie(body, item));
    }

    private WebClient getWebClient() {
        if (webClient.isEmpty()) {
            var headers = createHeaders(username, password);
            headers.add("Content-Type", MediaType.APPLICATION_JSON.toString());
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());

            var jira = appProperties.getDefault().getJira();
            this.webClient = Optional.of(WebClient
                    .builder()
                    .defaultHeaders((x) -> {
                        x.addAll(headers);
                    })
                    .baseUrl(jira.getUrl())
                    .build());
        }
        return this.webClient.get();
    }

    private Mono<IndicatorDto> convertie(String body, Indicator item) {
        try {
            IndicatorDto indicatorDto = new IndicatorDto();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(body);
            TableDto tableDto = new TableDto();
            var listIssue = root.get("issues");
            if (listIssue != null && listIssue.isArray()) {
                for (var i = 0; i < listIssue.size(); i++) {
                    if (tableDto.getHeaders() == null) {
                        tableDto.setHeaders(new ArrayList<>());
                        tableDto.getHeaders().add("key");
                        tableDto.getHeaders().add("url");
                        tableDto.getHeaders().add("lastUpdate");
                        tableDto.getHeaders().add("statut");
                        tableDto.getHeaders().add("summary");
                        tableDto.getHeaders().add("description");
                    }
                    var line = new ArrayList<String>();
                    if (tableDto.getRows() == null) {
                        tableDto.setRows(new ArrayList<>());
                    }
                    tableDto.getRows().add(line);
                    var elt = listIssue.get(i);
                    var key = toString(elt.get("key"));
                    var urlIssue = toString(elt.get("self"));
                    var lastUpdate = "";
                    var statut = "";
                    var summary = "";
                    var description = "";
                    if (elt.has("fields")) {
                        var fields = elt.get("fields");
                        lastUpdate = toString(fields.get("updated"));
                        if (fields.get("status") != null) {
                            statut = toString(fields.get("status").get("name"));
                        }
                        summary = toString(fields.get("summary"));
                        description = toString(fields.get("description"));
                    }
                    line.add(key);
                    line.add(urlIssue);
                    line.add(lastUpdate);
                    line.add(statut);
                    line.add(summary);
                    line.add(description);
                }
                indicatorDto.setTable(tableDto);
                if (StringUtils.hasLength(item.getMinWidth())) {
                    if (indicatorDto.getCardStyle() == null) {
                        indicatorDto.setCardStyle(new HashMap<>());
                    }
                    indicatorDto.getCardStyle().put("min-width", item.getMinWidth());
                    indicatorDto.setMinwidth(item.getMinWidth());
                }
                if (StringUtils.hasLength(item.getMaxWidth())) {
                    if (indicatorDto.getCardStyle() == null) {
                        indicatorDto.setCardStyle(new HashMap<>());
                    }
                    indicatorDto.getCardStyle().put("max-width", item.getMaxWidth());
                }
                return Mono.just(indicatorDto);
            }
        } catch (IOException e) {
            LOGGER.error("Erreur pour lire le flux", e);
            return Mono.error(e);
        }
        return Mono.empty();
    }
}
