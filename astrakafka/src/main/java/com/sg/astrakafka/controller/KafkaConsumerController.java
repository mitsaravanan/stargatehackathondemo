package com.sg.astrakafka.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.sg.astrakafka.httputil.HttpUtil;

//Kafka consumer class

@RestController
@Configuration
@ComponentScan(basePackageClasses = HttpUtil.class)
public class KafkaConsumerController {
	Logger logger = LoggerFactory.getLogger(KafkaConsumerController.class);
	@Autowired
	private HttpUtil httpUtil;
	
	String token = null;

	//Listen for messages on kafka topic and insert them into Astra as documents.
	
	@KafkaListener(topics = { "iot-data-event" })
	public void getTopics(@RequestBody String iotrecord) {
		logger.debug("The auth token is" + token );
		if (token == null)
		{
			token=httpUtil.getToken();
		}
		logger.debug("The message from kafka topic" + iotrecord );
		httpUtil.insertAstra(iotrecord, token);

	}

}