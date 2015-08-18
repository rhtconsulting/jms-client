package com.redhat.consulting.hornetq_client;

import java.io.Serializable;
import java.util.HashMap;

public class JMSMessageObject implements Serializable {

    private static final long serialVersionUID = 1L;

    private HashMap<String, Object> properties = new HashMap<String, Object>();
    private HashMap<String, Object> metaData = new HashMap<String, Object>();
    private Serializable payload = null;

    /**
     * Get the metaData.
     * 
     * @return the metaData
     */
    public HashMap<String, Object> getMetaData() {
        return this.metaData;
    }

    /**
     * Set the metaData.
     * 
     * @param metaData the metaData to set
     */
    public void setMetaData(HashMap<String, Object> metaData) {
        this.metaData = metaData;
    }

    /**
     * Get the properties.
     * 
     * @return the properties
     */
    public HashMap<String, Object> getProperties() {
        return this.properties;
    }

    /**
     * Set the properties.
     * 
     * @param properties the properties to set
     */
    public void setProperties(HashMap<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Get the payload.
     * 
     * @return the payload
     */
    public Serializable getPayload() {
        return this.payload;
    }

    /**
     * Set the payload.
     * 
     * @param payload the payload to set
     */
    public void setPayload(Serializable payload) {
        this.payload = payload;
    }
    
    

}
