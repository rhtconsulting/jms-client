package com.redhat.consulting.hornetq_client;

public class Main {

    private static String CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static String DESTINATION = "jms/queue/test";
    private static String USERNAME = "admin";
    private static String PASSWORD = "admin";
    private static String PROVIDER_URL = "remote://localhost:4447";
    private static String MODE = "consumer";
    private static String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static String SERVER = "hornetq";

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Client");

        processArgs(args);

        // Set Server Mode (HornetQ/AMQ)
        String icf = INITIAL_CONTEXT_FACTORY;
        if(Main.SERVER.equalsIgnoreCase("amq")){
            icf = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
        }
        
        if (Main.MODE.equalsIgnoreCase("consumer")) {
            System.out.println("Creating Consumer");
            ClientConsumer consumer = new ClientConsumer(DESTINATION, USERNAME, PASSWORD, PROVIDER_URL, CONNECTION_FACTORY, icf);

            System.out.println("Starting Consumer");
            consumer.consumeToFilesystem();
        } else {
            System.out.println("Creating Producer");
            ClientProducer producer = new ClientProducer(DESTINATION, USERNAME, PASSWORD, PROVIDER_URL, CONNECTION_FACTORY, icf);

            System.out.println("Starting Producer");
            producer.pushFromFilesystem();
        }

        System.out.println("Stopping Client");
        System.exit(1);
    }

    public static void processArgs(String[] args) {
        System.out.println("Processing Arguments");

        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-help")) {
                System.out.println("Displaying Help");
                displayHelp();
                return;
            } else if (args[i].equalsIgnoreCase("-d")) {
                Main.DESTINATION = args[++i];
                System.out.println("Setting Destination Name: " + Main.DESTINATION);
            } else if (args[i].equalsIgnoreCase("-cf")) {
                Main.CONNECTION_FACTORY = args[++i];
                System.out.println("Setting Connection Factory: " + Main.CONNECTION_FACTORY);
            } else if (args[i].equalsIgnoreCase("-u")) {
                Main.USERNAME = args[++i];
                System.out.println("Setting Username: " + Main.USERNAME);
            } else if (args[i].equalsIgnoreCase("-p")) {
                Main.PASSWORD = args[++i];
                System.out.println("Setting Password: " + Main.PASSWORD);
            } else if (args[i].equalsIgnoreCase("-pu")) {
                Main.PROVIDER_URL = args[++i];
                System.out.println("Setting Provider URL: " + Main.PROVIDER_URL);
            } else if (args[i].equalsIgnoreCase("-pr") || args[i].equalsIgnoreCase("-m") ) {
                Main.MODE = "producer";
                System.out.println("Setting Mode: " + Main.MODE);
            } else if (args[i].equalsIgnoreCase("-amq")) {
                Main.SERVER = "amq";
                System.out.println("Setting Server Type: " + Main.SERVER);
            } else if (args[i].equalsIgnoreCase("-ks")) {
                System.setProperty("javax.net.ssl.keyStore", args[++i]);
                System.out.println("Setting Keystore: " + System.getProperty("javax.net.ssl.keyStore"));
            } else if (args[i].equalsIgnoreCase("-ksp")) {
                System.setProperty("javax.net.ssl.keyStorePassword", args[++i]);
                System.out.println("Setting Keystore Password: " + System.getProperty("javax.net.ssl.keyStorePassword"));
            } else if (args[i].equalsIgnoreCase("-ts")) {
                System.setProperty("javax.net.ssl.trustStore", args[++i]);
                System.out.println("Setting Truststore: " + System.getProperty("javax.net.ssl.trustStore"));
            } else if (args[i].equalsIgnoreCase("-tsp")) {
                System.setProperty("javax.net.ssl.trustStorePassword", args[++i]);
                System.out.println("Setting Truststore Password: " + System.getProperty("javax.net.ssl.trustStorePassword"));
            }

        }
    }

    public static void displayHelp() {
        System.out.println("JMS File System Client Help");
        System.out.println("----------------------------------");
        System.out.println("Syntax:");
        System.out.println("   java -jar jms-client-jar-with-dependencies.jar [options]");
        System.out.println("Options:");
        System.out.println("   -p    Account Password");
        System.out.println("   -u    Account Username");
        System.out.println("   -d    Name of Queue/Topic");
        System.out.println("   -pu   Remote Provider URL ");
        System.out.println("   -cf   Name of Remote Connection Factory");
        System.out.println("   -pr   Sets to Producer mode for Producing Messages. Default Mode is Consumer.");
        System.out.println("   -amq  Sets to ActiveMQ(AMQ) Mode. Default Mode is for HornetQ");
        System.out.println("   -ks   Sets the path to the Keystore (For SSL Only)");
        System.out.println("   -ksp  Sets the Keystore Password (For SSL Only)");
        System.out.println("   -ts   Sets the path to the Truststore (For SSL Only)");
        System.out.println("   -tsp  Sets the Truststore Password (For SSL Only)");
    }

}
