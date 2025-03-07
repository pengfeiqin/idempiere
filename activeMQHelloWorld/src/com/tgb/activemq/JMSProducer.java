package com.tgb.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
/**
 * 消息的生产者（发送者） 
 * @author liang
 *
 */
public class JMSProducer {

    //默认连接用户名
    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
    //默认连接密码
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    //默认连接地址
    private static String BROKEURL = ActiveMQConnection.DEFAULT_BROKER_URL;
    
//    private static String BROKEURL = "stomp://localhost:61613";
    //发送的消息数量
    private static final int SENDNUM = 1;

    public static void main(String[] args) {
        //连接工厂
        ConnectionFactory connectionFactory;
        //连接
        Connection connection = null;
        //会话 接受或者发送消息的线程
        Session session;
        //消息的目的地
        Destination destination;
        //消息生产者
        MessageProducer messageProducer;
        //实例化连接工厂
        connectionFactory = new ActiveMQConnectionFactory(JMSProducer.USERNAME, JMSProducer.PASSWORD, JMSProducer.BROKEURL);

        try {
            //通过连接工厂获取连接
            connection = connectionFactory.createConnection();
            //启动连接
            connection.start();
            //创建session
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            //创建一个名称为HelloWorld的消息队列
            destination = session.createQueue("HelloWorld");
            //创建消息生产者
            messageProducer = session.createProducer(destination);
            //发送消息
            sendMessage(session, messageProducer);

            session.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(connection != null){
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 发送消息
     * @param session
     * @param messageProducer  消息生产者
     * @throws Exception
     */
    public static void sendMessage(Session session,MessageProducer messageProducer) throws Exception{
        for (int i = 0; i < JMSProducer.SENDNUM; i++) {
            //创建一条文本消息 
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"orderId\":");
            sb.append(i);
            sb.append(",");
            sb.append("\"bomItemId\":");
            sb.append(10011);
            sb.append(",");
            sb.append("\"bomQty\":");
            sb.append(1000);
            sb.append(",");
            sb.append("\"bomCosting\":");
            sb.append(1000.11);
            sb.append(",");
            sb.append("\"setupTime\":");
            sb.append("\"2013-06-05T00:00:00+00:00\"");
            sb.append(",");
            sb.append("\"processTime\":");
            sb.append("\"2012-06-05T00:00:00+00:00\"");
            sb.append(",");
            sb.append("\"scrap\":");
            sb.append(17);
            sb.append(",");
            sb.append("\"expectDelDate\":");
            sb.append("\"2014-06-05T00:00:00+00:00\"");
            sb.append(",");
            sb.append("\"planedStartDate\":");
            sb.append("\"2016-06-05T00:00:00+00:00\"");
            sb.append(",");
            sb.append("\"endDate\":");
            sb.append("\"2015-06-05T00:00:00+00:00\"");
            sb.append("}");
            TextMessage message = session.createTextMessage(sb.toString());
            System.out.println("发送消息：Activemq 发送消息" + i);
            //通过消息生产者发出消息 
            messageProducer.send(message);
        }
    }
}