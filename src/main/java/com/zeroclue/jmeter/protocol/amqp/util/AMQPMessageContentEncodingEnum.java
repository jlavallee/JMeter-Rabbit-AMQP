package com.zeroclue.jmeter.protocol.amqp.util;

/**
 * Created by ricardotaboada on 5/14/15.
 */
public enum AMQPMessageContentEncodingEnum {
    UTF_8("UTF-8"), GZIP_UTF_8("gzip:UTF-8");

    private String encodingString;

    AMQPMessageContentEncodingEnum(String encodingString) {
        this.encodingString = encodingString;
    }

    public String getEncodingString() {
        return encodingString;
    }
}
