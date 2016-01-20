# jms-migrator
JMS Client for Importing/Exporting Serialized Messages from HornetQ and ActiveMQ

## Usage
*Syntax:*
  java -jar jms-client-jar-with-dependencies.jar [options]
*Options:*
  -p Account Password
  -u Account Username
  -d Name of Queue/Topic
  -pu Remote Provider URL 
  -cf Name of Remote Connection Factory
  -pr Sets to Producer mode for Producing Messages. Default Mode is Consumer.
  -amq Sets to ActiveMQ(AMQ) Mode. Default Mode is for HornetQ
  -ks Sets the path to the Keystore (For SSL Only)
  -ksp Sets the Keystore Password (For SSL Only)
  -ts Sets the path to the Truststore (For SSL Only)
  -tsp Sets the Truststore Password (For SSL Only)
