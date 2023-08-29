package org.projetmanager.projetmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.tomcat.util.codec.binary.Base64;
import org.projetmanager.projetmanager.dto.TableDto;
import org.projetmanager.projetmanager.model.BitBucketAccesToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;


import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;

public class BitbucketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BitbucketService.class);

    private final String clientId;

    private final String secretKey;

    private final String urlAuth;
    private final String url;

    private Cache<Integer, BitBucketAccesToken> cacheToken;

    private final RestTemplate restTemplate;

    public BitbucketService(String clientId, String secretKey, String urlAuth, String url) {
        this.clientId = clientId;
        this.secretKey = secretKey;
        this.urlAuth = urlAuth;
        this.url = url;
        cacheToken = Caffeine.newBuilder().build();
        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    public String test1() {

        var token=getToken();


        if(StringUtils.hasText(token)) {

            var headers = createHeadersBearer(token);
            headers.add("Accept", MediaType.ALL_VALUE);

            var httpEntity = new HttpEntity<>(headers);

            var url=this.url+"/testdashboard1/commits";
            LOGGER.info("url={}",url);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);


            LOGGER.info("response={}", response);
            LOGGER.info("body={}", response.getBody());
        }

        return "OK";
    }

    private String getToken() {
        var tokenCached=cacheToken.getIfPresent(1);
        if(tokenCached!=null){
            return tokenCached.getAccessToken();
        } else {

            var accesKey=appelToken();

            if(accesKey!=null) {

                var token = accesKey.getAccessToken();
                long expireInSeconde=accesKey.getExpiresIn();
                cacheToken.policy().expireAfterWrite().ifPresent(expiration -> {
                    expiration.setExpiresAfter(Duration.ofSeconds(expireInSeconde));
                });
                cacheToken.put(1, accesKey);

                return token;
            } else {
                return null;
            }
        }
    }

    private BitBucketAccesToken appelToken(){

        var headers = createHeaders(clientId, secretKey);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED.toString());
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("grant_type", "client_credentials");

        var httpEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<BitBucketAccesToken> response = restTemplate.exchange(urlAuth, HttpMethod.POST, httpEntity, BitBucketAccesToken.class);

        LOGGER.info("response={}", response);

        if(response.hasBody()) {
            LOGGER.info("body={}", response.getBody());

            return response.getBody();
        } else {
            return null;
        }
    }

    private HttpHeaders createHeaders(String username, String password) {
        var headers=new HttpHeaders();
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(StandardCharsets.US_ASCII),false);
        String authHeader = "Basic " + new String(encodedAuth);
        headers.add("Authorization", authHeader);
        return headers;
    }

    private HttpHeaders createHeadersBearer(String token) {
        var headers=new HttpHeaders();
        String authHeader = "Bearer " + token;
        headers.add("Authorization", authHeader);
        return headers;
    }

    public TableDto getPullRequest() {

        var token=getToken();


        if(StringUtils.hasText(token)) {


            TableDto tableDto = new TableDto();

            var headers = createHeadersBearer(token);
            headers.add("Accept", MediaType.ALL_VALUE);

            var httpEntity = new HttpEntity<>(headers);

            var url=this.url+"/testdashboard1/pullrequests";
            LOGGER.info("url={}",url);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);


            LOGGER.info("response={}", response);
            LOGGER.info("body={}", response.getBody());
            if(response.hasBody()){
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(response.getBody());
                    var listValues = root.get("values");

                    if (listValues != null && listValues.isArray()) {
                        for (var i = 0; i < listValues.size(); i++) {
                            if (tableDto.getHeaders() == null) {
                                tableDto.setHeaders(new ArrayList<>());
                                tableDto.getHeaders().add("titre");
                                tableDto.getHeaders().add("status");
                                tableDto.getHeaders().add("description");
                                tableDto.getHeaders().add("url");
                            }
                            var line = new ArrayList<String>();
                            if (tableDto.getRows() == null) {
                                tableDto.setRows(new ArrayList<>());
                            }
                            tableDto.getRows().add(line);
                            var elt = listValues.get(i);
                            var titre = toString(elt.get("title"));
                            var status = toString(elt.get("state"));
                            var description = toString(elt.get("description"));
                            line.add(titre);
                            line.add(status);
                            line.add(description);
                            String htmlLink2="";
                            if(elt.has("links")){
                                var link=elt.get("links");
                                var htmlLink=link.get("html");
                                htmlLink2=toString(htmlLink.get("href"));
                            }
                            line.add(htmlLink2);
                        }
                        afficheAlerte(listValues);
                    }

                    return tableDto;

                }catch(Exception e){
                    LOGGER.error("Erreur", e);
                }
            }
        }

        return null;
    }

    private void afficheAlerte(JsonNode listValues) throws AWTException {
        if(listValues!=null&&listValues.isArray()&&!listValues.isEmpty()){
            int nb=listValues.size();
            if(nb>0){

                SwingUtilities.invokeLater(() -> {
                    //createAndShowGUI();
                    try {
                        displayTray("il y a des PR : "+nb,"Il y a "+nb+" PR à regarder");
                    } catch (AWTException e) {
                        LOGGER.atError().log("Erreur",e);
                    }
                });

//                displayTray("il y a des PR : "+nb,"Il y a "+nb+" PR à regarder");
            }
        }
    }

    private void displayTray(String message, String message2) throws AWTException {
        if (SystemTray.isSupported()) {
            //Obtain only one instance of the SystemTray object
            SystemTray tray = SystemTray.getSystemTray();

            //If the icon is a file
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            //Alternative (if the icon is on the classpath):
            //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

            TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
            //Let the system resize the image if needed
            trayIcon.setImageAutoSize(true);
            //Set tooltip text for the tray icon
            trayIcon.setToolTip("System tray icon demo");
            tray.add(trayIcon);

            trayIcon.displayMessage(message, message2, TrayIcon.MessageType.INFO);
        } else {
            LOGGER.atWarn().log("System tray not supported !");
        }
    }

    private String toString(JsonNode node) {
        if (node == null) {
            return "";
        } else {
            return node.asText();
        }
    }

}
