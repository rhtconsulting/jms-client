package com.redhat.consulting.hornetq_client;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;

public class ClientConsumer {

    private String connectionFactoryName;
    private String destinationName;
    private String username;
    private String password;
    private String initialContextFactory;
    private String providerUrl;

    public ClientConsumer(String p_destination, String p_username, String p_password, String p_providerUrl,
            String p_connectionFactory, String p_initialContextFactory) {
        connectionFactoryName = p_connectionFactory;
        destinationName = p_destination;
        username = p_username;
        password = p_password;
        providerUrl = p_providerUrl;
        initialContextFactory = p_initialContextFactory;
    }

    public void consumeToFilesystem() throws Exception {

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

            // Create a Queue Browser
            QueueBrowser queueBrowser = session.createBrowser(queue);

            // Start Connection
            connection.start();

            // Get Messages
            Enumeration e = queueBrowser.getEnumeration();

            // Create Destination Name Directory
            Files.createDirectories(Paths.get(destinationName));

            // Loop Over All Messages on Queue
            while (e.hasMoreElements()) {
                Message message = (Message) e.nextElement();

                System.out.println("Received Message: " + message.getJMSMessageID());

                JMSMessageObject msgObject = WrapperUtility.createWrapperObject(message);

                // Serialize the Message to Disk
                String fileName = destinationName + "/msg_" + message.getJMSTimestamp() + "-"
                        + System.currentTimeMillis() + ".ser";
                FileOutputStream fileOut = new FileOutputStream(fileName);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(msgObject);
                out.close();
                fileOut.close();

                System.out.println("Serialized Message to " + fileName);
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
