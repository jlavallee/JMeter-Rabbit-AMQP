package com.zeroclue.jmeter.protocol.amqp;

import com.rabbitmq.client.AMQP;
import java.io.IOException;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.rabbitmq.client.Channel;

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

    private static final long serialVersionUID = -8420658040465788497L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    //++ These are JMX names, and must not be changed
    private final static String MESSAGE = "AMQPPublisher.Message";
    private final static String MESSAGE_ROUTING_KEY = "AMQPPublisher.MessageRoutingKey";
    private final static String MESSAGE_TYPE = "AMQPPublisher.MessageType";
    private final static String REPLY_TO_QUEUE = "AMQPPublisher.ReplyToQueue";
    private final static String CORRELATION_ID = "AMQPPublisher.CorrelationId";

    public static boolean DEFAULT_PERSISTENT = false;
    private final static String PERSISTENT = "AMQPConsumer.Persistent";

    private transient Channel channel;

    public AMQPPublisher() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SampleResult sample(Entry e) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setSuccessful(false);
        result.setResponseCode("500");

        try {
            initChannel();
        } catch (Exception ex) {
            log.error("Failed to initialize channel : ", ex);
            result.setResponseMessage(ex.toString());
            return result;
        }

        String data = getMessage(); // Sampler data

        result.setSampleLabel(getTitle());
        /*
         * Perform the sampling
         */

        int loop = getIterationsAsInt();
        result.sampleStart(); // Start timing
        try {

            for (int idx = 0; idx < loop; idx++) {
                // try to force jms semantics.
                // but this does not work since RabbitMQ does not sync to disk if consumers are connected as
                // seen by iostat -cd 1. TPS value remains at 0.

                //if (getPersistent()) {
                //    channel.txSelect();
                //}
                channel.basicPublish(getExchange(), getMessageRoutingKey(), getProperties(), getMessageBytes());
                //if (getPersistent()) {
                //    channel.txCommit();
                //}
            }

            /*
             * Set up the sample result details
             */
            result.setSamplerData(data);
            result.setResponseData("OK", null);
            result.setDataType(SampleResult.TEXT);

            result.setResponseCodeOK();
            result.setResponseMessage("OK");
            result.setSuccessful(true);
        } catch (Exception ex) {
            log.debug(ex.getMessage(), ex);
            result.setResponseCode("000");
            result.setResponseMessage(ex.toString());
        }
        finally {
            result.sampleEnd(); // End timimg
        }

        return result;
    }


    private byte[] getMessageBytes() {
        return getMessage().getBytes();
    }

    /**
     * @return the message routing key for the sample
     */
    public String getMessageRoutingKey() {
        return getPropertyAsString(MESSAGE_ROUTING_KEY);
    }

    public void setMessageRoutingKey(String content) {
        setProperty(MESSAGE_ROUTING_KEY, content);
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

    /**
     * @return the message type for the sample
     */
    public String getMessageType() {
        return getPropertyAsString(MESSAGE_TYPE);
    }

    public void setMessageType(String content) {
        setProperty(MESSAGE_TYPE, content);
    }

    /**
     * @return the reply-to queue for the sample
     */
    public String getReplyToQueue() {
        return getPropertyAsString(REPLY_TO_QUEUE);
    }

    public void setReplyToQueue(String content) {
        setProperty(REPLY_TO_QUEUE, content);
    }

    /**
     * @return the correlation identifier for the sample
     */
    public String getCorrelationId() {
        return getPropertyAsString(CORRELATION_ID);
    }

    public void setCorrelationId(String content) {
        setProperty(CORRELATION_ID, content);
    }


    public Boolean getPersistent() {
        return getPropertyAsBoolean(PERSISTENT, DEFAULT_PERSISTENT);
    }

    public void setPersistent(Boolean persistent) {
       setProperty(PERSISTENT, persistent);
    }

    @Override
    public boolean interrupt() {
        cleanup();
        return true;
    }

    @Override
    protected Channel getChannel() {
        return channel;
    }

    @Override
    protected void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    protected AMQP.BasicProperties getProperties() {
        AMQP.BasicProperties parentProps = super.getProperties();

        AMQP.BasicProperties publishProperties =
                new AMQP.BasicProperties(parentProps.getContentType(), parentProps.getContentEncoding(),
                parentProps.getHeaders(), getPersistent() ? 2 : parentProps.getDeliveryMode(), parentProps.getPriority(),
                getCorrelationId(), getReplyToQueue(), parentProps.getExpiration(),
                parentProps.getMessageId(), parentProps.getTimestamp(), getMessageType(),
                parentProps.getUserId(), parentProps.getAppId(), parentProps.getClusterId());

        return publishProperties;
    }

    protected boolean initChannel() throws IOException{
        boolean ret = super.initChannel();
        return ret;
    }
}