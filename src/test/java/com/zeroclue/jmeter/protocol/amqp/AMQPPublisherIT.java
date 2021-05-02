package com.zeroclue.jmeter.protocol.amqp;

import com.rabbitmq.client.ConnectionFactory;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AMQPPublisherIT {

    private static final Logger logger = LoggerFactory.getLogger(AMQPPublisherIT.class);

    private static final String ROUTING_KEY = "routingKey";
    private static final String EXCHANGE = "exchange";
    private static final String QUEUE = "queue";
    private AMQPPublisher amqpPublisher;

    @BeforeEach
    private void init() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        logger.info("init");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setHost("localhost");
        factory.setPort(5672);
        amqpPublisher = new AMQPPublisher(factory);
        amqpPublisher.setRoutingKey(ROUTING_KEY);
        amqpPublisher.setMessageRoutingKey(ROUTING_KEY);
        amqpPublisher.setQueue(QUEUE);
        amqpPublisher.setExchange(EXCHANGE);
        amqpPublisher.setUsername("guest");
        amqpPublisher.setPassword("guest");
        amqpPublisher.setVirtualHost("/");
        amqpPublisher.setHost("localhost");
        amqpPublisher.setPort("5672");
        amqpPublisher.setQueueRedeclare(true);
        amqpPublisher.setExchangeRedeclare(true);
        amqpPublisher.setExchangeType("direct");
        amqpPublisher.initChannel();
    }

    @Test
    @DisplayName("Should not throw exception when channel is init twice")
    void channelDoubleInit() {
        assertDoesNotThrow(amqpPublisher::initChannel);
        amqpPublisher.sample(new Entry());
    }

    @Test
    @DisplayName("Sample")
    void collectSample() {
        SampleResult result = amqpPublisher.sample(new Entry());
        assertNotNull(result);
        assertTrue(result.isSuccessful());
        assertEquals(1, result.getSubResults().length);
    }

    @ParameterizedTest(name = "collect {0} samples isSuccessful={1} with {0} messages (auto ack)")
    @CsvSource({
                       "0, true",
                       "10, true",
                       "-1, true",
                       "5, true",
                       "1, true",
               })
    void collectSamples(String samples, boolean success) throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        amqpPublisher.setIterations(samples);
        amqpPublisher.setPersistent(true);
        SampleResult result = amqpPublisher.sample(new Entry());
        assertEquals(Integer.parseInt(samples), result.getSampleCount());
        assertEquals(success, result.isSuccessful());
        Awaitility.await()
                  .atMost(2, TimeUnit.SECONDS)
                  .until(() -> Math.max(amqpPublisher.getIterationsAsInt(), 0) == amqpPublisher.getChannel().messageCount(QUEUE));
        assertEquals(Math.max(amqpPublisher.getIterationsAsInt(), 0), amqpPublisher.getChannel().messageCount(QUEUE));
    }


}
