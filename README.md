# Stargate Hackathon Demo - Kafka Ingest into Astra - Document API

This project demostrates the how to ingest data from Kafka to Astra using **Stargate schemaless document API**.

## Tools 
- Spring Boot
- Kafka producer
- spring-kafka consumer
- Stargate schemaless document API for Astra
- Spring Boot Rest Template (For testing deep nested Json documents) 

## Prerequisites 
- Create zookeeper and kafka server and start them
- Create a kafka topic 
- Create a kafka producer publish messages to the Kafka topic created
- Create Astra DB with stargate enabled.

## Project:1 -> (# astrakafka) -> Kafka Consumer
- Subscribe to the topic created above using spring boot Kafka Listerner. The listener will listen on the topic for the kafka messages
- As the messages arrive in the topic, the listener process them and ingests them into Astra db using Stargate Schema less Document API. 
- Verify the documents are written to Astra DB

## Project:2 -> (# stargateapidemo) -> Rest Template
- Validate variuos Document API using Rest Template. Some of the use cases are as follows.
 - Create a deep nested JSON document with multiple levels
 - Access the entire document
 - Access the document at various levels 
 - Search the document with variuos condtions like **gt, lt,  eq**
 
 ## Observations\Comments
 - Need the ability to process batch JSON documents like DSBULK.
 - Possibility to extend kafka connector to ingest kakfa payloads as documents directly.
 - When a deep nested JSON is created, some times produces the error “org.apache.cassandra.stargate.exceptions.WriteTimeoutException: Operation timed out - received only 0 responses.”
