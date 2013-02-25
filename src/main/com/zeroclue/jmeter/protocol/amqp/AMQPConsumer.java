package com.zeroclue.jmeter.protocol.amqp;

import java.io.IOException;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class AMQPConsumer extends AMQPSampler implements Interruptible, TestStateListener {

    private static final long serialVersionUID = 7480863561320459091L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    //++ These are JMX names, and must not be changed
    private final static String PURGE_QUEUE = "AMQPConsumer.PurgeQueue";
    private final static String AUTO_ACK = "AMQPConsumer.AutoAck";
    private final static String RECEIVE_TIMEOUT = "AMQPConsumer.ReceiveTimeout";

    private transient Channel channel;

    public AMQPConsumer(){
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SampleResult sample(Entry entry) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setSuccessful(false);
        result.setResponseCode("500");

        QueueingConsumer consumer;
        String consumerTag;

        trace("AMQPConsumer.sample()");

        try {
            initChannel();
            
            consumer = new QueueingConsumer(channel);
            channel.basicQos(1); // TODO: make prefetchCount configurable?
            consumerTag = channel.basicConsume(getQueue(), autoAck(), consumer);
        } catch (IOException ex) {
            log.error("Failed to initialize channel", ex);
            return result;
        }

        result.setSampleLabel(getTitle());
        /*
         * Perform the sampling
         */
        result.sampleStart(); // Start timing
        try {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery(getReceiveTimeoutAsInt());

            if(delivery == null){
                log.warn("nextDelivery timed out");
                return result;
            }

            /*
             * Set up the sample result details
             */
            result.setSamplerData(new String(delivery.getBody()));

            result.setResponseData("OK", null);
            result.setDataType(SampleResult.TEXT);

            result.setResponseCodeOK();
            result.setResponseMessage("OK");
            result.setSuccessful(true);

            if(!autoAck())
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

        } catch (ShutdownSignalException e) {
            log.warn("AMQP consumer failed to consume", e);
            result.setResponseCode("400");
            result.setResponseMessage(e.toString());
            interrupt();
        } catch (ConsumerCancelledException e) {
            log.warn("AMQP consumer failed to consume", e);
            result.setResponseCode("300");
            result.setResponseMessage(e.toString());
            interrupt();
        } catch (InterruptedException e) {
            log.info("interuppted while attempting to consume");
            result.setResponseCode("200");
            result.setResponseMessage(e.toString());
        } catch (IOException e) {
            log.warn("AMQP consumer failed to consume", e);
            result.setResponseCode("100");
            result.setResponseMessage(e.toString());
        } finally {
            try {
                channel.basicCancel(consumerTag);
            } catch(IOException e) {
                log.error("Couldn't safely cancel the sample's consumer", e);
            }
        }

        result.sampleEnd(); // End timimg
        trace("AMQPConsumer.sample ended");

        return result;
    }

    @Override
    protected Channel getChannel() {
        return channel;
    }

    @Override
    protected void setChannel(Channel channel) {
        this.channel = channel;
    }

    /**
     * @return the whether or not to purge the queue
     */
    public String getPurgeQueue() {
        return getPropertyAsString(PURGE_QUEUE);
    }

    public void setPurgeQueue(String content) {
        setProperty(PURGE_QUEUE, content);
    }

    public void setPurgeQueue(Boolean purgeQueue) {
        setProperty(PURGE_QUEUE, purgeQueue.toString());
    }

    public boolean purgeQueue(){
        return Boolean.parseBoolean(getPurgeQueue());
    }

    /**
     * @return the whether or not to auto ack
     */
    public String getAutoAck() {
        return getPropertyAsString(AUTO_ACK);
    }

    public void setAutoAck(String content) {
        setProperty(AUTO_ACK, content);
    }

    public void setAutoAck(Boolean autoAck) {
        setProperty(AUTO_ACK, autoAck.toString());
    }

    public boolean autoAck(){
        return getPropertyAsBoolean(AUTO_ACK);
    }

    protected int getReceiveTimeoutAsInt() {
        if (getPropertyAsInt(RECEIVE_TIMEOUT) < 1) {
            return DEFAULT_TIMEOUT;
        }
        return getPropertyAsInt(RECEIVE_TIMEOUT);
    }

    public String getReceiveTimeout() {
        return getPropertyAsString(RECEIVE_TIMEOUT, DEFAULT_TIMEOUT_STRING);
    }


    public void setReceiveTimeout(String s) {
        setProperty(RECEIVE_TIMEOUT, s);
    }

    @Override
    public boolean interrupt() {
        testEnded();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void testEnded() {
        if(purgeQueue()){
            log.info("Purging queue " + getQueue());
            try {
                channel.queuePurge(getQueue());
            } catch (IOException e) {
                log.error("Failed to purge queue " + getQueue(), e);
            }
        }
    }

    @Override
    public void testEnded(String arg0) {

    }

    @Override
    public void testStarted() {

    }

    @Override
    public void testStarted(String arg0) {

    }

    /*
     * Helper method
     */
    private void trace(String s) {
        String tl = getTitle();
        String tn = Thread.currentThread().getName();
        String th = this.toString();
        log.debug(tn + " " + tl + " " + s + " " + th);
    }
}
