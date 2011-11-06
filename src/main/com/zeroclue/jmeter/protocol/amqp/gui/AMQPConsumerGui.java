package com.zeroclue.jmeter.protocol.amqp.gui;

import javax.swing.JPanel;

import org.apache.jmeter.testelement.TestElement;

import com.zeroclue.jmeter.protocol.amqp.AMQPConsumer;


public class AMQPConsumerGui extends AMQPSamplerGui {

    private static final long serialVersionUID = 1L;
    
    private JPanel mainPanel;
    
    @Override
    public String getStaticLabel() {
        return "AMQP Consumer";
    }

    @Override
    protected void setMainPanel(JPanel panel) {
        mainPanel = panel;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TestElement createTestElement() {
        AMQPConsumer sampler = new AMQPConsumer();
        modifyTestElement(sampler);
        return sampler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabelResource() {
        return this.getClass().getSimpleName();
    }


}
