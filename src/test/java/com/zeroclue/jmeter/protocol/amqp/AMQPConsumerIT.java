package com.zeroclue.jmeter.protocol.amqp;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AMQPConsumerIT {

    private static final String ROUTING_KEY = "routingKey";
    private static final String EXCHANGE = "exchange";
    private static final String QUEUE = "queue";
    private AMQPConsumer amqpConsumer;
    Logger logger = LoggerFactory.getLogger(AMQPConsumerIT.class);

    @BeforeEach
    private void init() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        logger.info("init");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setHost("localhost");
        factory.setPort(5672);
        //factory.setVirtualHost("/");
        amqpConsumer = new AMQPConsumer(factory);
        amqpConsumer.setRoutingKey(ROUTING_KEY);
        amqpConsumer.setQueue(QUEUE);
        amqpConsumer.setExchange(EXCHANGE);
        amqpConsumer.setUsername("guest");
        amqpConsumer.setPassword("guest");
        amqpConsumer.setVirtualHost("/");
        amqpConsumer.setHost("localhost");
        amqpConsumer.setPort("5672");
        amqpConsumer.setQueueRedeclare(true);
        amqpConsumer.setExchangeRedeclare(true);
        amqpConsumer.setExchangeType("direct");
        amqpConsumer.initChannel();
    }

    @Test
    @DisplayName("Instantiate AMQPConsumer with default values")
    void checkConsumerInstantiation() {
        assertNotNull(amqpConsumer);
        assertTrue(amqpConsumer.getChannel().isOpen());
    }

    @ParameterizedTest(name = "initChannel with useTx={0} and prefetchCount={1}")
    @CsvSource({
                       "true,    1",
                       "false,    1",
                       "true,    -1000",
                       "false,    -1000",
                       "true,    0",
                       "false,    0",
               })
    void instantiateConsumerWithTransactions(boolean useTx, String prefetchCount) {
        amqpConsumer.setUseTx(useTx);
        amqpConsumer.setPrefetchCount(prefetchCount);
        assertEquals(amqpConsumer.getUseTx(), useTx);
        assertEquals(amqpConsumer.getPrefetchCount(), prefetchCount);
        assertDoesNotThrow(amqpConsumer::initChannel);
    }

    @Test
    @DisplayName("Should not throw exception when channel is init twice")
    void channelDoubleInit() {
        assertTrue(amqpConsumer.getChannel().isOpen());
        amqpConsumer.sample(new Entry());
    }

    @Test
    @DisplayName("Should recreate AMQP Channel if it is closed")
    void recreateChannel() {
        //opens channel
        amqpConsumer.sample(new Entry());
        //close channel manually
        assertDoesNotThrow(() -> amqpConsumer.getChannel().close());
        //restart channel
        amqpConsumer.sample(new Entry());
        //verify channel is open
        assertTrue(amqpConsumer.getChannel().isOpen());
    }

    @Test
    @DisplayName("Sample")
    void collectSample() {
        SampleResult result = amqpConsumer.sample(new Entry());
        assertNotNull(result);
        assertFalse(result.isSuccessful());
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
    void collectSamplesAutoAck(String samples, boolean success) throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        amqpConsumer.setAutoAck("true");
        amqpConsumer.setIterations(samples);
        enqueue(amqpConsumer.getChannel(), amqpConsumer.getIterationsAsInt());

        SampleResult result = amqpConsumer.sample(new Entry());
        assertEquals(Integer.parseInt(samples), result.getSampleCount());
        assertEquals(success, result.isSuccessful());
        assertEquals(0, amqpConsumer.getChannel().messageCount(QUEUE));
    }

    @ParameterizedTest(name = "collect {0} samples isSuccessful={1} with {0} messages (no auto ack)")
    @CsvSource({
                       "0, true",
                       "10, true",
                       "-1, true",
                       "5, true",
                       "1, true",
               })
    void collectSamplesNoAutoAck(String samples, boolean success) throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        amqpConsumer.setAutoAck("false");
        amqpConsumer.setIterations(samples);
        enqueue(amqpConsumer.getChannel(), amqpConsumer.getIterationsAsInt());

        SampleResult result = amqpConsumer.sample(new Entry());
        assertEquals(Integer.parseInt(samples), result.getSampleCount());
        assertEquals(success, result.isSuccessful());
        assertEquals(0, amqpConsumer.getChannel().messageCount(QUEUE));
    }

    private void enqueue(Channel channel, int samples) {
        IntStream.range(0, samples)
                 .forEach(it -> {
                     try {
                         channel.basicPublish(
                                 EXCHANGE,
                                 ROUTING_KEY,
                                 new AMQP.BasicProperties(),
                                 ("Message #" + it).getBytes(StandardCharsets.UTF_8));
                     } catch (IOException e) {
                         throw new RuntimeException(e);
                     }
                 });
    }

}
