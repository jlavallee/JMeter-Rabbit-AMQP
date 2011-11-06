package com.zeroclue.jmeter.protocol.amqp;

import java.io.IOException;

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * JMeter creates an instance of a sampler class for every occurrence of the
 * element in every thread. [some additional copies may be created before the
 * test run starts]
 *
 * Thus each sampler is guaranteed to be called by a single thread - there is no
 * need to synchronize access to instance variables.
 *
 * However, access to class fields must be synchronized.
 */
public class AMQPPublisher extends AbstractSampler {

    private static final long serialVersionUID = 240L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    //++ These are JMX names, and must not be changed
    
    
    private static final String EXCHANGE = "AMQPSampler.Exchange"; // $NON-NLS-1$
    private static final String QUEUE = "AMQPSampler.Queue"; // $NON-NLS-1$
    private static final String ROUTING_KEY = "AMQPSampler.RoutingKey"; // $NON-NLS-1$
    private static final String VIRUTAL_HOST = "AMQPSampler.VirtualHost"; // $NON-NLS-1$
    private static final String HOST = "AMQPSampler.Host"; // $NON-NLS-1$
    private static final String PORT = "AMQPSampler.Port"; // $NON-NLS-1$
    private static final String USERNAME = "AMQPSampler.Username"; // $NON-NLS-1$
    private static final String PASSWORD = "AMQPSampler.Password"; // $NON-NLS-1$
    private static final String TIMEOUT = "AMQPSampler.timeout"; // $NON-NLS-1$
    
    private final static String MESSAGE = "AMQPSampler.message"; //$NON-NLS-1$



    private static final int DEFAULT_TIMEOUT = 1000;
    public static final String DEFAULT_TIMEOUT_STRING = Integer.toString(DEFAULT_TIMEOUT);

    private static final int DEFAULT_PORT = 5672;
    public static final String DEFAULT_PORT_STRING = Integer.toString(DEFAULT_PORT);


    private transient ConnectionFactory factory;
    private transient Connection connection;
    private transient Channel channel;


    public AMQPPublisher() {
        trace("AMQPSampler()");
        factory = new ConnectionFactory();
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public SampleResult sample(Entry e) {
        trace("sample()");
        SampleResult res = new SampleResult();
        boolean isOK = false; // Did sample succeed?
        String data = getMessage(); // Sampler data
        String response = null;

        res.setSampleLabel(getTitle());
        /*
         * Perform the sampling
         */
        res.sampleStart(); // Start timing
        try {

            // Do something here ...

            response = Thread.currentThread().getName();

            /*
             * Set up the sample result details
             */
            res.setSamplerData(data);
            res.setResponseData(response, null);
            res.setDataType(SampleResult.TEXT);

            res.setResponseCodeOK();
            res.setResponseMessage("OK");// $NON-NLS-1$
            isOK = true;
        } catch (Exception ex) {
            log.debug("", ex);
            res.setResponseCode("500");// $NON-NLS-1$
            res.setResponseMessage(ex.toString());
        }
        res.sampleEnd(); // End timimg

        res.setSuccessful(isOK);

        return res;
    }

    /**
     * @return a string for the sampleResult Title
     */
    private String getTitle() {
        return this.getName();
    }

    /**
     * @return the message for the sample
     */
    public String getMessage() {
        return getPropertyAsString(MESSAGE);
    }

    public void setMessage(String content) {
        setProperty(MESSAGE, content);
    }
    
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
    
    private int getPortAsInt() {
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

    private int getTimeoutAsInt() {
        if (getPropertyAsInt(TIMEOUT) < 1) {
            return DEFAULT_TIMEOUT;
        }
        return getPropertyAsInt(TIMEOUT);
    }

    public String getTimeout() {
        return getPropertyAsString(TIMEOUT, DEFAULT_TIMEOUT_STRING);
    }


    public void setTimeout(String s) {
        setProperty(TIMEOUT, s);
    }

    /*
     * Helper method
     */
    private void trace(String s) {
        String tl = getTitle();
        String tn = Thread.currentThread().getName();
        String th = this.toString();
    }
}