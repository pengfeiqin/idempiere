package com.activemq;

import java.sql.SQLException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import net.sf.json.JSONObject;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQTextMessage;

import com.dao.OrderDaoA;

public class JMSListenerB implements MessageListener {

	@Override
	public void onMessage(Message message) {
		String jsonString = new String();
		if (message instanceof ActiveMQBytesMessage){
			jsonString = new String (((ActiveMQBytesMessage) message).getContent().getData());
		}else if (message instanceof ActiveMQTextMessage){
			jsonString = new String (((ActiveMQTextMessage) message).getContent().getData());
		}else if (message instanceof TextMessage){
			try {
				jsonString = ((TextMessage) message).getText();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		if (jsonString != null){
			System.out.println("B" + jsonString);
			try {
				//转换为javaBean
				JSONObject jsonObject = JSONObject.fromObject(jsonString); 
				String orderId = String.valueOf((jsonObject.get("orderId")));
				System.out.println("orderId");
				OrderDaoA.updateStatus(orderId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

}
