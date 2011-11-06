package com.zeroclue.jmeter.protocol.amqp;

import java.io.IOException;
import org.apache.log.Logger;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;

public class AMQPConsumer extends AMQPSampler implements Interruptible {

    private static final long serialVersionUID = 1L;
 
    private static final Logger log = LoggingManager.getLoggerForClass();
 
    private transient Channel channel;
    
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
            // TODO: perhaps we should start a real consumer?
            GetResponse resp = channel.basicGet(getQueue(), true);
            
            /*
             * Set up the sample result details
             */
            result.setSamplerData(new String(resp.getBody()));
            result.setResponseData("OK", null);
            result.setDataType(SampleResult.TEXT);

            result.setResponseCodeOK();
            result.setResponseMessage("OK");// $NON-NLS-1$
            result.setSuccessful(true);
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

    @Override
    protected Channel getChannel() {
        return channel;
    }

    @Override
    protected void setChannel(Channel channel) {
        this.channel = channel;
    }
}
