package com.redhat.consulting.hornetq_client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;

public class ClientProducer {
    private String connectionFactoryName;
    private String destinationName;
    private String username;
    private String password;
    private String initialContextFactory;
    private String providerUrl;

    public ClientProducer(String p_destination, String p_username, String p_password, String p_providerUrl,
            String p_connectionFactory, String p_initialContextFactory) {
        connectionFactoryName = p_connectionFactory;
        destinationName = p_destination;
        username = p_username;
        password = p_password;
        providerUrl = p_providerUrl;
        initialContextFactory = p_initialContextFactory;
    }


    public void pushFromFilesystem() throws Exception {

        // Define Needed Variables
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        Queue queue = null;
        Context context = null;

        try {
            // Create Context Properties
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
            env.put(Context.PROVIDER_URL, providerUrl);
            env.put(Context.SECURITY_PRINCIPAL, username);
            env.put(Context.SECURITY_CREDENTIALS, password);

            // Create Context
            context = new InitialContext(env);

            // Lookup Connection Factory
            connectionFactory = (ConnectionFactory) context.lookup(connectionFactoryName);

            // Lookup Destination
            try {
                queue = (Queue) context.lookup(destinationName);
            } catch (NameNotFoundException nnfe) {
                System.out.println("Queue Not Found, Trying with Dynamic Mapping...");
                queue = (Queue) context.lookup("dynamicQueues/" + destinationName);
            }

            // Create Connection
            connection = connectionFactory.createConnection(username, password);

            // Create Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create a Producer
            MessageProducer producer = (MessageProducer) session.createProducer(queue);

            // Start Connection
            connection.start();

            // Get Messages
            File[] listOfFiles = new File[1];
            if(Files.isDirectory(Paths.get(destinationName))){
                File importDirectory = new File(destinationName);
                listOfFiles = importDirectory.listFiles(new SerializedFileFilter());
            } else{
                throw new FileNotFoundException("No Directory for Destination Messages");
            }
            
            // Push Message to Queue
            for(File file : listOfFiles){
                System.out.println("Processing File "+file.getAbsolutePath());
                
                // Read in File
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                JMSMessageObject msgObject = (JMSMessageObject) objectInputStream.readObject();
                objectInputStream.close();
                fileInputStream.close();
                
                // Create Object Message
                String msgId = (String) msgObject.getMetaData().get(WrapperUtility.KEY_JMS_MESSAGE_ID);
                System.out.println("Creating JMS Message with ID " + msgId);
               
                String msgType = (String) msgObject.getMetaData().get(WrapperUtility.KEY_MSG_TYPE);
                if(msgType.equalsIgnoreCase("TextMessage")){
                    TextMessage message = session.createTextMessage();
                    message.setText((String)msgObject.getPayload());
                    WrapperUtility.updateMessage(msgObject, message);
                    
                    // Push Message
                    producer.send(message);
                    System.out.println("Sent Text Message " + msgId);
                }else if(msgType.equalsIgnoreCase("ObjectMessage")){
                    ObjectMessage message = session.createObjectMessage();
                    message.setObject(msgObject.getPayload());
                    WrapperUtility.updateMessage(msgObject, message);
                    
                    // Push Message
                    producer.send(message);
                    System.out.println("Sent Object Message " + msgId);
                }else{
                    System.out.println("Message Payload Not Supported for Message ID " + msgId);
                }
                
            }
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if (context != null) {
                context.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}
