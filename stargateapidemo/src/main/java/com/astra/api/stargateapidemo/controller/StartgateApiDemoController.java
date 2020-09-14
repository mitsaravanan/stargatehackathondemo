package com.astra.api.stargateapidemo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.io.IOUtils;

@CrossOrigin(origins = "http://localhost:3000")
@Component
@Getter @Setter
@ConfigurationProperties(prefix = "astra.connect.properties")
@RestController
public class StartgateApiDemoController {
	
	Logger logger = LoggerFactory.getLogger(StartgateApiDemoController.class);
	private String userName;
	private String password;
	private String keySpace;
	private String dbId;
	private String region;
	private String tableName;
	private String token;
	private String authUrl = "/api/rest/v1/auth";
	private String docUrl ="/api/rest/v2/namespaces/";
	private String astraBaseUrl = "https://" +dbId + "-" + region + "." + "apps.astra.datastax.com";
	
	
	@Autowired
	RestTemplate restTemplate;
	
    @RequestMapping(value = "/api/v2", method = RequestMethod.GET)
    public ModelAndView root() {
        return new ModelAndView("redirect:/swagger-ui/");
    }

   //Get auth token
    
   @RequestMapping(value = "/api/v2/auth", method = RequestMethod.POST)
   public String getToken() {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      String url="https://" + dbId + "-" + region + "." + "apps.astra.datastax.com" + authUrl;
      
      JSONObject bodyParam = new JSONObject();
      bodyParam.put("username",userName);
      bodyParam.put("password",password);
      
      HttpEntity<String> requestEnty = new HttpEntity<>(bodyParam.toString(), headers);
      ResponseEntity<String> loginResponse = restTemplate.exchange(url, HttpMethod.POST, requestEnty, String.class);
      logger.info("Result - status ("+ loginResponse.getStatusCode() + ") has body: " + loginResponse.hasBody());
      logger.info("Response ="+loginResponse.getBody());
      JSONObject jsonobject = new JSONObject(loginResponse.getBody());
      token = jsonobject.getString("authToken");
      logger.debug("The auth token is " + token);
      return token;
   }
   
   //Create Json document in Astra db 
   
   @RequestMapping(value = "/api/v2/createdoc", method = RequestMethod.POST)
   public void createDocument() {
      HttpHeaders headers = new HttpHeaders();
      File file=null;
      JSONObject json=null;
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.add("x-cassandra-token",token);
      String url="https://" + dbId + "-" + region + "." + "apps.astra.datastax.com" + docUrl +  keySpace +  "/collections/" +"bike1" + "/";
      
      try {
		file =  new ClassPathResource("bikelocation.json").getFile();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      //String exampleRequest = FileUtils.readFileToString(new File("exampleJsonRequest.json"), StandardCharsets.UTF_8);
      if (file.exists()){
          InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          String jsonTxt = null;
		try {
			jsonTxt = IOUtils.toString(is, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          System.out.println(jsonTxt);
          json = new JSONObject(jsonTxt);       

      }
      HttpEntity<String> requestEnty = new HttpEntity<>(json.toString(), headers);
      ResponseEntity<String> loginResponse = restTemplate.exchange(url, HttpMethod.POST, requestEnty, String.class);
      logger.info("Result - status ("+ loginResponse.getStatusCode() + ") has body: " + loginResponse.hasBody());
      logger.info("Response ="+loginResponse.getBody());
      JSONObject jsonobject = new JSONObject(loginResponse.getBody());
      logger.debug("Data Add step: " + jsonobject.toString());
      
   }
   
   //Access the whole document from Astra table
   
   @RequestMapping(value = "/api/v2/docroot", method = RequestMethod.GET)
   public String rootDoc() {
    HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.add("x-cassandra-token",token);
      String url="https://" + dbId + "-" + region + "." + "apps.astra.datastax.com" + docUrl +  keySpace +  "/collections/" +"bikecoordinate" + "/" + "bike";
      System.out.println("Token is " + token);
      HttpEntity<String> requestEnty = new HttpEntity<>(headers);
      ResponseEntity<String> loginResponse = restTemplate.exchange(url, HttpMethod.GET, requestEnty, String.class);
      logger.info("Result - status ("+ loginResponse.getStatusCode() + ") has body: " + loginResponse.hasBody());
      logger.info("Response ="+loginResponse.getBody());
      JSONObject jsonobject = new JSONObject(loginResponse.getBody());
	  return  jsonobject.toString(4);
	   
   }
   
 //Access deep nested Json documents 
   
   @RequestMapping(value = "/api/v2/docroot/level6", method = RequestMethod.GET)
   public String rootDocLevel6() {
    HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.add("x-cassandra-token",token);
      String url="https://" + dbId + "-" + region + "." + "apps.astra.datastax.com" + docUrl +  keySpace +  
    		  "/collections/" +"bikecoordinate/bike/features/[3]/properties/bikes";
      System.out.println("Token is " + token);
      HttpEntity<String> requestEnty = new HttpEntity<>(headers);
      ResponseEntity<String> loginResponse = restTemplate.exchange(url, HttpMethod.GET, requestEnty, String.class);
      logger.info("Result - status ("+ loginResponse.getStatusCode() + ") has body: " + loginResponse.hasBody());
      logger.info("Response ="+loginResponse.getBody());
      JSONObject jsonobject = new JSONObject(loginResponse.getBody());
	  return  jsonobject.toString(4);
	   
   }

//Step to test filter documents data
   
   @RequestMapping(value = "/api/v2/docroot/search", method = RequestMethod.GET)
   public String searchDoc() {
    HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.add("x-cassandra-token",token);
      String url="https://" + dbId + "-" + region + "." + "apps.astra.datastax.com" + docUrl +  keySpace +  
    		  "/collections/" +"bikecoordinate" + "/" + "bike" + "/" + "features" + "?where={ \"data.[0].properties.totalDocks\": { \"$eq\": \"17\" }}" ;
      System.out.println("Token is " + token);
      HttpEntity<String> requestEnty = new HttpEntity<>(headers);
      ResponseEntity<String> loginResponse = restTemplate.exchange(url, HttpMethod.GET, requestEnty, String.class);
      logger.info("Result - status ("+ loginResponse.getStatusCode() + ") has body: " + loginResponse.hasBody());
      logger.info("Response ="+loginResponse.getBody());
      JSONObject jsonobject = new JSONObject(loginResponse.getBody());
	  return  jsonobject.toString(4);
	   
   }

   
   

}
