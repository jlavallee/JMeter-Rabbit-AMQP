package com.zeroclue.jmeter.protocol.amqp.util;

import com.rabbitmq.client.QueueingConsumer.Delivery;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

/**
 * Created by ricardotaboada on 5/14/15.
 */
public class GzipMessageEncodingUtil implements AMQPMessageContentEncodingUtil {

    /**
     * Decodes a Gzip message
     *
     * @param message
     *
     * @return
     */

    public Delivery decode(Delivery message) {

        try {
            return postProcessMessage(message);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private Delivery postProcessMessage(Delivery message) throws Exception {

        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(message.getBody()));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        String outStr = "";
        String line;
        while ((line = bf.readLine()) != null) {
            outStr += line;
        }

        Delivery delivery = new Delivery(message.getEnvelope(), message.getProperties(), outStr.getBytes());

        return delivery;

    }
}
