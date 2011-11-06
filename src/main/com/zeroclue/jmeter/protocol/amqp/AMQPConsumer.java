package com.zeroclue.jmeter.protocol.amqp;

import java.io.IOException;
import org.apache.log.Logger;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.QueueingConsumer;

public class AMQPConsumer extends AMQPSampler implements Interruptible {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    //++ These are JMX names, and must not be changed
    private final static String PURGE_QUEUE = "AMQPConsumer.purgeQueue"; //$NON-NLS-1$

    private transient Channel channel;
    private transient QueueingConsumer consumer;

    protected boolean initChannel() throws IOException {
        if(super.initChannel()){
            consumer = new QueueingConsumer(channel);
            // TODO allow setting autoAck
            channel.basicConsume(getQueue(), false, consumer);
            return true;
        }
        return false;
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
            //GetResponse resp = channel.basicGet(getQueue(), true);

            QueueingConsumer.Delivery delivery = consumer.nextDelivery(getTimeoutAsInt());

            /*
             * Set up the sample result details
             */
            //result.setSamplerData(new String(resp.getBody()));
            result.setSamplerData(new String(delivery.getBody()));

            result.setResponseData("OK", null);
            result.setDataType(SampleResult.TEXT);

            result.setResponseCodeOK();
            result.setResponseMessage("OK");// $NON-NLS-1$
            result.setSuccessful(true);

            // process delivery
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        } catch (Exception ex) {
            log.debug("", ex);
            result.setResponseCode("000");// $NON-NLS-1$
            result.setResponseMessage(ex.toString());
        }

        result.sampleEnd(); // End timimg

        return result;
    }

    @Override
    public boolean interrupt() {
        testEnded();
        return true;
    }
    /**
     * {@inheritDoc}
     */
    public void testEnded() {
        if(purgeQueue()){
            log.info("Purging queue " + getQueue());
            try {
                channel.queuePurge(getQueue());
            } catch (IOException e) {
                log.error("Failed to purge queue " + getQueue(), e);
            }
        }
        super.testEnded();
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
}
