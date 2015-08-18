package com.redhat.consulting.hornetq_client;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

public class WrapperUtility {

    public static final String KEY_JMS_CORRELATION_ID = "JMSCorrelationID";
    public static final String KEY_JMS_DELIVERY_MODE = "JMSDeliveryMode";
    public static final String KEY_JMS_EXPIRATION = "JMSExpiration";
    public static final String KEY_JMS_TIMESTAMP = "JMSTimestamp";
    public static final String KEY_JMS_MESSAGE_ID = "JMSMessageID";
    public static final String KEY_JMS_PRIORITY = "JMSPriority";
    public static final String KEY_JMS_REPLY_TO = "JMSReplyTo";
    public static final String KEY_JMS_TYPE = "JMSType";
    public static final String KEY_JMS_REDELIVERED = "JMSRedelivered";
    public static final String KEY_MSG_TYPE = "MessageType";

    public static JMSMessageObject createWrapperObject(Message message) throws JMSException {
        JMSMessageObject wrapper = new JMSMessageObject();

        // Get the Properties
        Enumeration srcProperties = message.getPropertyNames();
        while (srcProperties.hasMoreElements()) {
            String propertyName = (String) srcProperties.nextElement();
            wrapper.getProperties().put(propertyName, message.getObjectProperty(propertyName));
        }

        // Get the JMS Meta Data
        wrapper.getMetaData().put(KEY_JMS_CORRELATION_ID, message.getJMSCorrelationID());
        wrapper.getMetaData().put(KEY_JMS_DELIVERY_MODE, message.getJMSDeliveryMode());
        wrapper.getMetaData().put(KEY_JMS_EXPIRATION, message.getJMSExpiration());
        wrapper.getMetaData().put(KEY_JMS_TIMESTAMP, message.getJMSTimestamp());
        wrapper.getMetaData().put(KEY_JMS_MESSAGE_ID, message.getJMSMessageID());
        wrapper.getMetaData().put(KEY_JMS_PRIORITY, message.getJMSPriority());
        wrapper.getMetaData().put(KEY_JMS_REPLY_TO, message.getJMSReplyTo());
        wrapper.getMetaData().put(KEY_JMS_TYPE, message.getJMSType());
        wrapper.getMetaData().put(KEY_JMS_REDELIVERED, message.getJMSRedelivered());

        // Get the Payload
        if (message instanceof TextMessage) {
            TextMessage txtMessage = (TextMessage) message;
            wrapper.setPayload(txtMessage.getText());
            wrapper.getMetaData().put(KEY_MSG_TYPE, "TextMessage");
            System.out.println("Processed Text Message");
        } else if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            wrapper.setPayload(objMessage.getObject());
            wrapper.getMetaData().put(KEY_MSG_TYPE, "ObjectMessage");
            System.out.println("Processed Object Message");
        } else {
            System.out.println("Message Payload Not Supported for Message ID " + message.getJMSMessageID());
        }

        return wrapper;
    }

    public static void updateMessage(JMSMessageObject msgObject, Message message) throws JMSException {
        // Set Properties
        HashMap<String, Object> properties = msgObject.getProperties();
        for(Map.Entry<String, Object> property: properties.entrySet()){
            message.setObjectProperty(property.getKey(), property.getValue());
        }
        
        // Set JMS Meta Data
        message.setJMSCorrelationID((String) msgObject.getMetaData().get(KEY_JMS_CORRELATION_ID));
        message.setJMSDeliveryMode((Integer) msgObject.getMetaData().get(KEY_JMS_DELIVERY_MODE));
        message.setJMSExpiration((Long) msgObject.getMetaData().get(KEY_JMS_EXPIRATION));
        message.setJMSTimestamp((Long) msgObject.getMetaData().get(KEY_JMS_TIMESTAMP));
        message.setJMSMessageID((String) msgObject.getMetaData().get(KEY_JMS_MESSAGE_ID));
        message.setJMSPriority((Integer) msgObject.getMetaData().get(KEY_JMS_PRIORITY));
        message.setJMSReplyTo((Destination) msgObject.getMetaData().get(KEY_JMS_REPLY_TO));
        message.setJMSType((String) msgObject.getMetaData().get(KEY_JMS_TYPE));
        message.setJMSRedelivered((Boolean) msgObject.getMetaData().get(KEY_JMS_REDELIVERED));
    }
}
