package com.zeroclue.jmeter.protocol.amqp.gui;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.apache.jmeter.testelement.TestElement;

import com.zeroclue.jmeter.protocol.amqp.AMQPConsumer;


public class AMQPConsumerGui extends AMQPSamplerGui {

    private static final long serialVersionUID = 1L;

    private final JCheckBox purgeQueue = new JCheckBox("Purge Queue", false); //$NON-NLS-1$

    private JPanel mainPanel;

    public AMQPConsumerGui(){
        init();
    }

    /*
     * Helper method to set up the GUI screen
     */
    protected void init() {
        super.init();

        mainPanel.add(purgeQueue);
    }

    @Override
    public String getStaticLabel() {
        return "AMQP Consumer";
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
    public void modifyTestElement(TestElement te) {
        AMQPConsumer sampler = (AMQPConsumer) te;
        sampler.clear();
        configureTestElement(sampler);

        super.modifyTestElement(sampler);

        sampler.setPurgeQueue(purgeQueue.isSelected());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabelResource() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void setMainPanel(JPanel panel) {
        mainPanel = panel;
    }
}
