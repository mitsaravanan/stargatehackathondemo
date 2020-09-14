package com.sg.astrakafka.httputil;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.UnirestException;


@Component
@Getter @Setter
@ConfigurationProperties(prefix = "astra.connect.properties")
public class HttpUtil {
	
	Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	private String userName;
	private String password;
	private String keySpace;
	private String dbId;
	private String region;
	private String tableName;
	private String token;
	HttpResponse<String> response=null;
	private String authUrl = "/api/rest/v1/auth";
	private String docUrl ="/api/rest/v2/namespaces/";
	private String astraBaseUrl = "https://" +dbId + "-" + region + "." + "apps.astra.datastax.com";
	

	// Get Auth token for Astra db access

public  String  getToken() {
	
	if (token == null)
	{
		Map<String, String> headers = new HashMap<>();
	    headers.put("content-type", "application/json");
	    JSONObject jsonBody = new JSONObject();
	    jsonBody.put("username", userName).put("password", password);
	    
		try {
			Unirest.setTimeouts(10000, 10000);
			response= Unirest.post("https://" + dbId + "-" + region + "." + "apps.astra.datastax.com" + authUrl)
			.headers(headers)
			.body(jsonBody)
			.asString();
			JSONObject jsonobject = new JSONObject(response.getBody());
			token = jsonobject.getString("authToken");
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	return token;
}
	
//Insert data into Astra db as document from kafka topic.

public  void  insertAstra(String iotrecord, String authToken) {
		Map<String, String> headers = new HashMap<>();
	    headers.put("content-type", "application/json");
	    headers.put("x-cassandra-token",authToken);
	    JSONObject jsonBody = new JSONObject(iotrecord.toString());
	    System.out.println(jsonBody.toString(4));
		/*
		 * try { TimeUnit.MINUTES.sleep(2); } catch (InterruptedException e1) { // TODO
		 * Auto-generated catch block e1.printStackTrace(); }
		 */
		try {
			Unirest.setTimeouts(10000, 10000);
			response= Unirest.post("https://" + dbId + "-" + region + "." +
			"apps.astra.datastax.com" + docUrl + keySpace +  "/collections/" +
			tableName + "/" )
			.headers(headers)
			.body(jsonBody)
			.asString();
			logger.debug("Mesage inserted into kafka" + response.getBody());
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

}

	
}
