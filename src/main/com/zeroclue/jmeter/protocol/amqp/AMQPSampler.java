package com.zeroclue.jmeter.protocol.amqp;

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;

public abstract class AMQPSampler extends AbstractSampler {

    public static final int DEFAULT_PORT = 5672;
    public static final String DEFAULT_PORT_STRING = Integer.toString(DEFAULT_PORT);

    //++ These are JMX names, and must not be changed
    protected static final String EXCHANGE = "AMQPSampler.Exchange"; // $NON-NLS-1$
    protected static final String QUEUE = "AMQPSampler.Queue"; // $NON-NLS-1$
    protected static final String ROUTING_KEY = "AMQPSampler.RoutingKey"; // $NON-NLS-1$
    protected static final String VIRUTAL_HOST = "AMQPSampler.VirtualHost"; // $NON-NLS-1$
    protected static final String HOST = "AMQPSampler.Host"; // $NON-NLS-1$
    protected static final String PORT = "AMQPSampler.Port"; // $NON-NLS-1$
    protected static final String USERNAME = "AMQPSampler.Username"; // $NON-NLS-1$
    protected static final String PASSWORD = "AMQPSampler.Password"; // $NON-NLS-1$
 
    public String getExchange() {
        return getPropertyAsString(EXCHANGE);
    }

    public void setExchange(String name) {
        setProperty(EXCHANGE, name);
    }


    public String getQueue() {
        return getPropertyAsString(QUEUE);
    }

    public void setQueue(String name) {
        setProperty(QUEUE, name);
    }


    public String getRoutingKey() {
        return getPropertyAsString(ROUTING_KEY);
    }

    public void setRoutingKey(String name) {
        setProperty(ROUTING_KEY, name);
    }


    public String getVirtualHost() {
        return getPropertyAsString(VIRUTAL_HOST);
    }

    public void setVirtualHost(String name) {
        setProperty(VIRUTAL_HOST, name);
    }


    public String getHost() {
        return getPropertyAsString(HOST);
    }

    public void setHost(String name) {
        setProperty(HOST, name);
    }


    public String getPort() {
        return getPropertyAsString(PORT);
    }

    public void setPort(String name) {
        setProperty(PORT, name);
    }

    protected int getPortAsInt() {
        if (getPropertyAsInt(PORT) < 1) {
            return DEFAULT_PORT;
        }
        return getPropertyAsInt(PORT);
    }



    public String getUsername() {
        return getPropertyAsString(USERNAME);
    }

    public void setUsername(String name) {
        setProperty(USERNAME, name);
    }


    public String getPassword() {
        return getPropertyAsString(PASSWORD);
    }

    public void setPassword(String name) {
        setProperty(PASSWORD, name);
    }
}
