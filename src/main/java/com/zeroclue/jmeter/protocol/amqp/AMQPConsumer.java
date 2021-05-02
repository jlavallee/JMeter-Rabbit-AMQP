package com.zeroclue.jmeter.protocol.amqp;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.ShutdownSignalException;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class AMQPConsumer extends AMQPSampler implements Interruptible, TestStateListener {
    private static final int DEFAULT_PREFETCH_COUNT = 0; // unlimited

    public static final boolean DEFAULT_READ_RESPONSE = true;
    public static final String DEFAULT_PREFETCH_COUNT_STRING = Integer.toString(DEFAULT_PREFETCH_COUNT);

    private static final long serialVersionUID = 7480863561320459091L;

    private static final Logger log = LoggerFactory.getLogger(AMQPPublisher.class);

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
    private transient String consumerTag;

    public AMQPConsumer() {
        super();
    }

    /**
     * constructor for testing purposes
     *
     * @param factory connection factory
     */
    AMQPConsumer(ConnectionFactory factory) {
        super(factory);
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
        result.setSampleLabel(getTitle());

        trace("AMQPConsumer.sample()");

        try {
            initChannel();
        } catch (Exception ex) {
            log.error("Failed to initialize channel", ex);
            result.setResponseMessage(ex.toString());
            return result;
        }

        /*
         * Perform the sampling
         */


        // aggregate samples.
        int loop = getIterationsAsInt();
        result.sampleStart(); // Start timing
        try {
            for (int idx = 0; idx < loop; idx++) {
                result.addSubResult(sample("Message " + idx, getReceiveTimeoutAsInt()));
            }

            // commit the sample.
            if (getUseTx()) {
                channel.txCommit();
            }

            result.setResponseData("Group Sample Completed", StandardCharsets.UTF_8.displayName());
            result.setDataType(SampleResult.TEXT);
            result.setResponseCodeOK();
            result.setSampleCount(loop);
            result.setSuccessful(Arrays.stream(result.getSubResults())
                    .allMatch(SampleResult::isSuccessful));

        } catch (ShutdownSignalException e) {
            consumerTag = null;
            log.warn("AMQP consumer failed to consume", e);
            result.setResponseCode("400");
            result.setSuccessful(false);
            result.setResponseMessage(e.getMessage());
            interrupt();
        } catch (ConsumerCancelledException e) {
            consumerTag = null;
            log.warn("AMQP consumer failed to consume", e);
            result.setResponseCode("300");
            result.setSuccessful(false);
            result.setResponseMessage(e.getMessage());
            interrupt();
        } catch (IOException e) {
            consumerTag = null;
            log.warn("AMQP consumer failed to consume", e);
            result.setResponseCode("100");
            result.setSuccessful(false);
            result.setResponseMessage(e.getMessage());
        } finally {
            result.sampleEnd(); // End timimg
        }

        trace("AMQPConsumer.sample ended");

        return result;
    }

    private SampleResult sample(String label, int receiveTimeoutAsInt) {
        Instant timeoutTime = Instant.now().plus(receiveTimeoutAsInt, ChronoUnit.MILLIS);
        trace("Sample Timeout: " + timeoutTime.toString());
        SampleResult sample = new SampleResult();
        sample.setSampleLabel(label);
        sample.sampleStart();
        while (timeoutTime.isAfter(Instant.now())) {
            trace("Timeout not expired, sampling new message");
            try {
                GetResponse response = channel.basicGet(getQueue(), autoAck());
                if (response != null) {
                    log.info("Message dequeued");
                    if (!autoAck()) {
                        try {
                            channel.basicAck(response.getEnvelope().getDeliveryTag(), false);
                        } catch (IOException e) {
                            return createErrorSample(sample, e.getMessage());
                        }
                    }
                    return createSuccessSample(sample, response);
                } else {
                    trace("no message received, sleeping");
                    try {
                        trace("sleeping");
                        Thread.sleep(500);
                        trace("end sleep");
                    } catch (InterruptedException e) {
                        return createErrorSample(sample, e.getMessage());
                    }
                }
            } catch (IOException e) {
                log.error("exception", e);
                return createErrorSample(sample, e.getMessage());
            }

        }
        trace("Timeout expired");
        return createErrorSample(sample, "timed out");

    }

    private SampleResult createSuccessSample(SampleResult sampleResult, GetResponse response) {
        if (getReadResponseAsBoolean()) {
            String message = new String(response.getBody(), StandardCharsets.UTF_8);
            sampleResult.setSamplerData(message);
            sampleResult.setResponseMessage(message);
        } else {
            sampleResult.setSamplerData("Body not available - \"Read Response\" set to false");
        }
        sampleResult.setResponseHeaders(formatHeaders(response.getProps(), response.getEnvelope()));
        sampleResult.setSuccessful(true);
        return sampleResult;
    }

    private SampleResult createErrorSample(SampleResult sample, String message) {
        SampleResult result = new SampleResult();
        result.setSuccessful(false);
        return result;
    }

    private String formatHeaders(AMQP.BasicProperties properties, Envelope envelope) {

        StringBuilder sb = new StringBuilder();
        sb.append(TIMESTAMP_PARAMETER).append(": ")
                .append(properties.getTimestamp() != null ? properties.getTimestamp().getTime() : "")
                .append("\n");
        sb.append(EXCHANGE_PARAMETER).append(": ").append(envelope.getExchange()).append("\n");
        sb.append(ROUTING_KEY_PARAMETER).append(": ").append(envelope.getRoutingKey()).append("\n");
        sb.append(DELIVERY_TAG_PARAMETER).append(": ").append(envelope.getDeliveryTag()).append("\n");

        Map<String, Object> headers = properties.getHeaders();
        if (headers != null) {
            for (String key : headers.keySet()) {
                sb.append(key).append(": ").append(headers.get(key)).append("\n");
            }
        }
        return sb.toString();
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

    public boolean purgeQueue() {
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

    public boolean autoAck() {
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

        if (purgeQueue()) {
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
        } catch (IOException e) {
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

    protected boolean initChannel() throws
            IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        boolean ret = super.initChannel();
        channel.basicQos(getPrefetchCountAsInt());
        if (getUseTx()) {
            channel.txSelect();
        }
        return ret;
    }
}
