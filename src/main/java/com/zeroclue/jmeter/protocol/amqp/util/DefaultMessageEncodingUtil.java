package com.zeroclue.jmeter.protocol.amqp.util;

import com.rabbitmq.client.QueueingConsumer.Delivery;

/**
 * Created by ricardotaboada on 5/14/15.
 */
public class DefaultMessageEncodingUtil implements AMQPMessageContentEncodingUtil {

    /**
     * As this is the default, it simply returns the received message
     *
     * @param message
     *
     * @return
     */
    public Delivery decode(Delivery message) {
        return message;
    }
}
