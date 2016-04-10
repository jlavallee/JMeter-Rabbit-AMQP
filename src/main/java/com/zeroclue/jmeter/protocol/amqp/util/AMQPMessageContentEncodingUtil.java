package com.zeroclue.jmeter.protocol.amqp.util;

import com.rabbitmq.client.QueueingConsumer.Delivery;

/**
 * Created by ricardotaboada on 5/14/15.
 */
public interface AMQPMessageContentEncodingUtil {

    Delivery decode(Delivery message);
}
