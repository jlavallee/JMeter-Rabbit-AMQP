package com.zeroclue.jmeter.protocol.amqp;

import java.io.IOException;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

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
public class AMQPPublisher extends AMQPSampler implements Interruptible {

    private static final long serialVersionUID = 240L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    private static final String TIMEOUT = "AMQPSampler.timeout"; // $NON-NLS-1$

    private final static String MESSAGE = "AMQPSampler.message"; //$NON-NLS-1$


    private static final int DEFAULT_TIMEOUT = 1000;
    public static final String DEFAULT_TIMEOUT_STRING = Integer.toString(DEFAULT_TIMEOUT);


    private transient ConnectionFactory factory;
    private transient Connection connection;
    private transient Channel channel;


    public AMQPPublisher() {
        factory = new ConnectionFactory();
    }

    /**
     * {@inheritDoc}
     */
    public SampleResult sample(Entry e) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setSuccessful(false);
        result.setResponseCode("500");

        try {
            initChannel();
        } catch (IOException ex) {
            log.error("Failed to initialize channel", ex);
            return result;
        }

        String data = getMessage(); // Sampler data

        result.setSampleLabel(getTitle());
        /*
         * Perform the sampling
         */
        result.sampleStart(); // Start timing
        try {
            channel.basicPublish(getExchange(), getRoutingKey(), getProperties(), getMessageBytes());
            /*
             * Set up the sample result details
             */
            result.setSamplerData(data);
            result.setResponseData("OK", null);
            result.setDataType(SampleResult.TEXT);

            result.setResponseCodeOK();
            result.setResponseMessage("OK");// $NON-NLS-1$
            result.setSuccessful(true);
        } catch (Exception ex) {
            log.debug("", ex);
            result.setResponseCode("000");// $NON-NLS-1$
            result.setResponseMessage(ex.toString());
        }

        result.sampleEnd(); // End timimg

        return result;
    }

    /**
     * the implementation calls testEnded() without any parameters.
     */
    public void testEnded(String host) {
        testEnded();
    }

    /**
     * endTest cleans up the client
     *
     * @see junit.framework.TestListener#endTest(junit.framework.Test)
     */
    public void testEnded() {
        log.debug("PublisherSampler.testEnded called");
        try {
            channel.close();
            connection.close();
        } catch (IOException e) {
            log.error("Failed to close channel or connection", e);
        }
    }

    private byte[] getMessageBytes() {
        return getMessage().getBytes();
    }

    // TODO: make this configurable
    private BasicProperties getProperties() {
        AMQP.BasicProperties properties = MessageProperties.PERSISTENT_TEXT_PLAIN;
        return properties;
    }

    private void initChannel() throws IOException {
        if(channel != null && channel.isOpen()){
            return;
        }
        if(channel != null && !channel.isOpen()){
            log.warn("channel " + channel.getChannelNumber()
                    + " closed unexpectedly: " + channel.getCloseReason().getLocalizedMessage());
        }
        factory.setConnectionTimeout(getTimeoutAsInt());
        factory.setVirtualHost(getVirtualHost());
        factory.setHost(getHost());
        factory.setPort(getPortAsInt());
        factory.setUsername(getUsername());
        factory.setPassword(getPassword());

        log.info("RabbitMQ ConnectionFactory using:"
                +"\n\t virtual host: " + getVirtualHost()
                +"\n\t host: " + getHost()
                +"\n\t port: " + getPort()
                +"\n\t username: " + getUsername()
                +"\n\t password: " + getPassword()
                +"\n\t timeout: " + getTimeout()
                );

        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(getExchange(), "direct", true);
        channel.queueDeclare(getQueue(), true, false, false, null);
        channel.queueBind(getQueue(), getExchange(), getRoutingKey());
        if(!channel.isOpen()){
            log.fatalError("Failed to open channel: " + channel.getCloseReason().getLocalizedMessage());
        }
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

    @Override
    public boolean interrupt() {
        testEnded();
        return true;
    }
}