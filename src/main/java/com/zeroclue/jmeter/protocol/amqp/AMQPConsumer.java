package com.zeroclue.jmeter.protocol.amqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class AMQPConsumer extends AMQPSampler implements Interruptible, TestStateListener {
    private static final int DEFAULT_PREFETCH_COUNT = 0; // unlimited

    public static final boolean DEFAULT_READ_RESPONSE = true;
    public static final String DEFAULT_PREFETCH_COUNT_STRING = Integer.toString(DEFAULT_PREFETCH_COUNT);

    private static final long serialVersionUID = 7480863561320459091L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    //++ These are JMX names, and must not be changed
    private static final String PREFETCH_COUNT = "AMQPConsumer.PrefetchCount";
    private static final String READ_RESPONSE = "AMQPConsumer.ReadResponse";
    private static final String PURGE_QUEUE = "AMQPConsumer.PurgeQueue";
    private static final String AUTO_ACK = "AMQPConsumer.AutoAck";
    private static final String RECEIVE_TIMEOUT = "AMQPConsumer.ReceiveTimeout";
    public static final String TIMESTAMP_PARAMETER = "Timestamp";
    public static final String EXCHANGE_PARAMETER = "Exchange";
    public static final String ROUTING_KEY_PARAMETER = "Routing Key";
    public static final String DELIVERY_TAG_PARAMETER = "Delivery Tag";

    public static boolean DEFAULT_USE_TX = false;
    private final static String USE_TX = "AMQPConsumer.UseTx";

    private transient Channel channel;
    private transient QueueingConsumer consumer;
    private transient String consumerTag;

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

        trace("AMQPConsumer.sample()");

        try {
            initChannel();

            // only do this once per thread. Otherwise it slows down the consumption by appx 50%
            if (consumer == null) {
                log.info("Creating consumer");
                consumer = new QueueingConsumer(channel);
            }
            if (consumerTag == null) {
                log.info("Starting basic consumer");
                consumerTag = channel.basicConsume(getQueue(), autoAck(), consumer);
            }
        } catch (Exception ex) {
            log.error("Failed to initialize channel", ex);
            result.setResponseMessage(ex.toString());
            return result;
        }

        result.setSampleLabel(getTitle());
        /*
         * Perform the sampling
         */

        // aggregate samples.
        int loop = getIterationsAsInt();
        result.sampleStart(); // Start timing
        QueueingConsumer.Delivery delivery = null;
        try {
            for (int idx = 0; idx < loop; idx++) {
                delivery = consumer.nextDelivery(getReceiveTimeoutAsInt());

                if(delivery == null){
                    result.setResponseMessage("timed out");
                    return result;
                }

                /*
                 * Set up the sample result details
                 */
                if (getReadResponseAsBoolean()) {
                    String response = new String(delivery.getBody());
                    result.setSamplerData(response);
                    result.setResponseMessage(response);
                }
                else {
                    result.setSamplerData("Read response is false.");
                }

                if(!autoAck())
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }

            // commit the sample.
            if (getUseTx()) {
                channel.txCommit();
            }

            result.setResponseData("OK", null);
            result.setDataType(SampleResult.TEXT);
            result.setResponseHeaders(delivery != null ? formatHeaders(delivery) : null);


            result.setResponseCodeOK();

            result.setSuccessful(true);

        } catch (ShutdownSignalException e) {
            consumer = null;
            consumerTag = null;
            log.warn("AMQP consumer failed to consume", e);
            result.setResponseCode("400");
            result.setResponseMessage(e.getMessage());
            interrupt();
        } catch (ConsumerCancelledException e) {
            consumer = null;
            consumerTag = null;
            log.warn("AMQP consumer failed to consume", e);
            result.setResponseCode("300");
            result.setResponseMessage(e.getMessage());
            interrupt();
        } catch (InterruptedException e) {
            consumer = null;
            consumerTag = null;
            log.info("interuppted while attempting to consume");
            result.setResponseCode("200");
            result.setResponseMessage(e.getMessage());
        } catch (IOException e) {
            consumer = null;
            consumerTag = null;
            log.warn("AMQP consumer failed to consume", e);
            result.setResponseCode("100");
            result.setResponseMessage(e.getMessage());
        } finally {
            result.sampleEnd(); // End timimg
        }

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

    public String getPrefetchCount() {
        return getPropertyAsString(PREFETCH_COUNT, DEFAULT_PREFETCH_COUNT_STRING);
    }

    public void setPrefetchCount(String prefetchCount) {
        setProperty(PREFETCH_COUNT, prefetchCount);
    }

    public int getPrefetchCountAsInt() {
        return getPropertyAsInt(PREFETCH_COUNT);
    }

    public Boolean getUseTx() {
        return getPropertyAsBoolean(USE_TX, DEFAULT_USE_TX);
    }

    public void setUseTx(Boolean tx) {
        setProperty(USE_TX, tx);
    }

    /**
     * set whether the sampler should read the response or not
     *
     * @param read whether the sampler should read the response or not
     */
    public void setReadResponse(Boolean read) {
        setProperty(READ_RESPONSE, read);
    }

    /**
     * return whether the sampler should read the response
     *
     * @return whether the sampler should read the response
     */
    public String getReadResponse() {
        return getPropertyAsString(READ_RESPONSE);
    }

    /**
     * return whether the sampler should read the response as a boolean value
     *
     * @return whether the sampler should read the response as a boolean value
     */
    public boolean getReadResponseAsBoolean() {
        return getPropertyAsBoolean(READ_RESPONSE);
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

    public void cleanup() {

        try {
            if (consumerTag != null) {
                channel.basicCancel(consumerTag);
            }
        } catch(IOException e) {
            log.error("Couldn't safely cancel the sample " + consumerTag, e);
        }

        super.cleanup();

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

    protected boolean initChannel() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        boolean ret = super.initChannel();
        channel.basicQos(getPrefetchCountAsInt());
        if (getUseTx()) {
            channel.txSelect();
        }
        return ret;
    }

    private String formatHeaders(QueueingConsumer.Delivery delivery){
        Map<String, Object> headers = delivery.getProperties().getHeaders();
        StringBuilder sb = new StringBuilder();
        sb.append(TIMESTAMP_PARAMETER).append(": ")
                .append(delivery.getProperties().getTimestamp() != null && delivery.getProperties().getTimestamp() != null ?
                        delivery.getProperties().getTimestamp().getTime() : "")
                .append("\n");
        sb.append(EXCHANGE_PARAMETER).append(": ").append(delivery.getEnvelope().getExchange()).append("\n");
        sb.append(ROUTING_KEY_PARAMETER).append(": ").append(delivery.getEnvelope().getRoutingKey()).append("\n");
        sb.append(DELIVERY_TAG_PARAMETER).append(": ").append(delivery.getEnvelope().getDeliveryTag()).append("\n");
        for (String key : headers.keySet()) {
            sb.append(key).append(": ").append(headers.get(key)).append("\n");
        }
        return sb.toString();
    }
}
