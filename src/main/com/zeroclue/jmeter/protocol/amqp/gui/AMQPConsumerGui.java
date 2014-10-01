package com.zeroclue.jmeter.protocol.amqp.gui;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledTextField;

import com.zeroclue.jmeter.protocol.amqp.AMQPConsumer;

import java.awt.*;


public class AMQPConsumerGui extends AMQPSamplerGui {

    private static final long serialVersionUID = 1L;

    protected JLabeledTextField receiveTimeout = new JLabeledTextField("Receive Timeout");
    protected JLabeledTextField prefetchCount = new JLabeledTextField("Prefetch Count");
    private final JCheckBox purgeQueue = new JCheckBox("Purge Queue", false);
    private final JCheckBox autoAck = new JCheckBox("Auto ACK", true);
    private final JCheckBox readResponse = new JCheckBox("Read Response", AMQPConsumer.DEFAULT_READ_RESPONSE);

    private JPanel mainPanel;

    public AMQPConsumerGui(){
        init();
    }

    /*
     * Helper method to set up the GUI screen
     */
    protected void init() {
        super.init();

        mainPanel.add(readResponse);

        prefetchCount.setPreferredSize(new Dimension(100,25));
        mainPanel.add(prefetchCount);

        mainPanel.add(receiveTimeout);
        mainPanel.add(purgeQueue);
        mainPanel.add(autoAck);
    }

    @Override
    public String getStaticLabel() {
        return "AMQP Consumer";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (!(element instanceof AMQPConsumer)) return;
        AMQPConsumer sampler = (AMQPConsumer) element;

        readResponse.setSelected(sampler.getReadResponseAsBoolean());
        prefetchCount.setText(sampler.getPrefetchCount());
        receiveTimeout.setText(sampler.getReceiveTimeout());
        purgeQueue.setSelected(sampler.purgeQueue());
        autoAck.setSelected(sampler.autoAck());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearGui() {
        super.clearGui();
        readResponse.setSelected(AMQPConsumer.DEFAULT_READ_RESPONSE);
        prefetchCount.setText(AMQPConsumer.DEFAULT_PREFETCH_COUNT_STRING);
        receiveTimeout.setText("");
        purgeQueue.setSelected(false);
        autoAck.setSelected(true);
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

        sampler.setReadResponse(readResponse.isSelected());
        sampler.setPrefetchCount(prefetchCount.getText());

        sampler.setReceiveTimeout(receiveTimeout.getText());
        sampler.setPurgeQueue(purgeQueue.isSelected());
        sampler.setAutoAck(autoAck.isSelected());

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
