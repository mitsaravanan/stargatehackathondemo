package com.sg.astrakafka.dao;

import java.nio.file.Paths;
import org.springframework.context.annotation.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.datastax.oss.driver.api.core.CqlSession;
import com.sg.astrakafka.controller.KafkaConsumerController;

import lombok.Getter;
import lombok.Setter;

//This file is for cassandra driver integration. Not used for this kafka ingest.

@Component
@Getter @Setter
@ConfigurationProperties(prefix = "astra.connect.properties")
public class SessionManager {
	Logger logger = LoggerFactory.getLogger(SessionManager.class);
    /** Singleton Pattern. */
    private static SessionManager _instance = null;
    
    /** Connectivity Attributes. */
    private String userName;
    private String password;
    private String keySpace;
    private String secureConnectionBundlePath;
    
    /** Status and working session. */
    private boolean initialized = true;
    private CqlSession cqlSession;
    
    public static final String QUERY_HEALTH_CHECK = "select data_center from system.local";
    
      

    public SessionManager() {
    	super();
	}

	public static synchronized SessionManager getInstance() {
        if (null == _instance) {
            _instance = new SessionManager();
        }
        return _instance;
    }


    public void testCredentials(String user, String passwd, String keyspce, String secureConnectionBundlePath) {
        try (CqlSession tmpSession = CqlSession.builder()
                .withCloudSecureConnectBundle(Paths.get(secureConnectionBundlePath))
                .withAuthCredentials(user, passwd)
                .withKeyspace(keyspce).build()) {
            tmpSession.execute(QUERY_HEALTH_CHECK);
        } catch(RuntimeException re) {
            throw new IllegalStateException(re);
        }
    }
    

    public CqlSession connectToAstra() {
        if (!isInitialized()) {
            throw new IllegalStateException("Please initialize the connection parameters first ");
        }
        if (null == cqlSession) {
            cqlSession = CqlSession.builder()
                    .withCloudSecureConnectBundle(Paths.get(getSecureConnectionBundlePath()))
                    .withAuthCredentials(getUserName(),getPassword())
                    .withKeyspace(getKeySpace())
                    .build();

        }
        return cqlSession;
    }
    

    public void checkConnection() {
        try {
            connectToAstra().execute(QUERY_HEALTH_CHECK);
        } catch(RuntimeException re) {
            throw new IllegalStateException(re);
        }
    }
    

    public void close() {
        if (isInitialized() && null != cqlSession) {
            cqlSession.close();
        }
    }


    public String getUserName() {
        return userName;
    }


    public String getPassword() {
        return password;
    }


    public String getSecureConnectionBundlePath() {
        return secureConnectionBundlePath;
    }


    public String getKeySpace() {
        return keySpace;
    }

    public boolean isInitialized() {
        return initialized;
    }    
    
}
