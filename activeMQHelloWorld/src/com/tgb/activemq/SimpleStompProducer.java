package com.tgb.activemq;

import org.apache.activemq.transport.stomp.StompConnection;

public class SimpleStompProducer {

	public static void main(String[] args) throws Exception{
		StompConnection connection = new StompConnection();
		connection.open("localhost", 61613);
		connection.connect("", "");
		connection.send("HelloWorld", "Hello World from " + SimpleStompProducer.class.getName());
		connection.disconnect();
	}
	
}
