package com.zeroclue.jmeter.protocol.amqp.util;

import com.rabbitmq.client.QueueingConsumer.Delivery;

/**
 * Created by ricardotaboada on 5/14/15.
 */
public class MessageContentEncodingUtilFactory {

    /**
     * Creates and Returns a new AMPQMessageContentEncodingUtil implementation for the contentEncoding provided in
     * the message.
     *
     * @param message
     *
     * @return
     *
     * @throws SkyWalkerException
     */
    public static AMQPMessageContentEncodingUtil create(Delivery message) throws Exception {

        if (message.getProperties().getContentEncoding() == null) {
            return new DefaultMessageEncodingUtil();
        }

        // Get the messageContentEncoding type
        AMQPMessageContentEncodingEnum amqpMessageContentEncodingEnum = getAmqpMessageContentEncodingEnum(message);

        // If no enum matched the encoding on the message then throw an exception
        if (amqpMessageContentEncodingEnum == null) {
            throw new Exception("Unsupported message encoding");
        }

        // Get the AMQPMessageContentEncodingUtil implementation for the encoding read from the message
        AMQPMessageContentEncodingUtil amqpMessageContentEncodingUtil = getAMQPMessageContentEncodingUtilImpl(amqpMessageContentEncodingEnum);

        if (amqpMessageContentEncodingUtil != null) {
            return amqpMessageContentEncodingUtil;
        }

        // If it reaches this statement, then the message encoding is unsupported. Throw an exception
        throw new Exception("Unsupported message encoding");
    }

    /**
     * Gets the Enum corresponding to the content Enconding of the message
     *
     * @param message
     *
     * @return
     */
    private static AMQPMessageContentEncodingEnum getAmqpMessageContentEncodingEnum(Delivery message) {

        AMQPMessageContentEncodingEnum amqpMessageContentEncodingEnum = null;

        for (AMQPMessageContentEncodingEnum amce : AMQPMessageContentEncodingEnum.values()) {
            if (amce.getEncodingString().equals(message.getProperties().getContentEncoding())) {
                amqpMessageContentEncodingEnum = amce;
            }
        }

        return amqpMessageContentEncodingEnum;
    }

    /**
     * Returns a implementation of the AMQPMessageContentEncodingUtil depending from the enum passed to the method
     *
     * @param amqpMessageContentEncodingEnum
     *
     * @return
     */
    private static AMQPMessageContentEncodingUtil getAMQPMessageContentEncodingUtilImpl(AMQPMessageContentEncodingEnum amqpMessageContentEncodingEnum) {

        switch (amqpMessageContentEncodingEnum) {
            case UTF_8:
                return new DefaultMessageEncodingUtil();
            case GZIP_UTF_8:
                return new GzipMessageEncodingUtil();
        }

        // if it reaches this statement then the message encoding is unsupported. Return null;
        return null;
    }
}
