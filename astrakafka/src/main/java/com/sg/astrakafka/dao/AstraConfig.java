package com.sg.astrakafka.dao;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

//Config details
@Getter @Setter
@ConfigurationProperties(prefix = "astra.connect.properties")
@Configuration("astraprop")
public class AstraConfig {
	private String   userName;
	private String   password;
	private String   keySpace;
	private String   secureConnectionBundlePath;
}
