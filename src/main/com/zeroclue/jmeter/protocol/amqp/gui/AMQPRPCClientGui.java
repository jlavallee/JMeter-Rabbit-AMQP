package com.zeroclue.jmeter.protocol.amqp.gui;

import java.awt.Dimension;

import javax.swing.*;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledTextArea;
import org.apache.jorphan.gui.JLabeledTextField;

import com.zeroclue.jmeter.protocol.amqp.AMQPRPCClient;

/**
 * AMQP Sampler
 *
 * This class is responsible for ensuring that the Sampler data is kept in step
 * with the GUI.
 *
 * The GUI class is not invoked in non-GUI mode, so it should not perform any
 * additional setup that a test would need at run-time
 *
 */
public class AMQPRPCClientGui extends AMQPSamplerGui {

    private static final long serialVersionUID = 1L;

    private JPanel mainPanel;

    /*
    private static final String[] CONFIG_CHOICES = {"File", "Static"};
    private final JLabeledRadio configChoice = new JLabeledRadio("Message Source", CONFIG_CHOICES);
    private final FilePanel messageFile = new FilePanel("Filename", ALL_FILES);
    */
    private JLabeledTextArea message = new JLabeledTextArea("Message Content");
    private JLabeledTextField messageRoutingKey = new JLabeledTextField("Routing Key");
    private JLabeledTextField messageType = new JLabeledTextField("Message Type");
    private JLabeledTextField replyToQueue = new JLabeledTextField("Reply-To Queue");
    private JLabeledTextField correlationId = new JLabeledTextField("Correlation Id");
    private final JCheckBox autoAck = new JCheckBox("Auto ACK", true);

    private JCheckBox persistent = new JCheckBox("Persistent?", AMQPRPCClient.DEFAULT_PERSISTENT);
    private JCheckBox useTx = new JCheckBox("Use Transactions?", AMQPRPCClient.DEFAULT_USE_TX);

    private ArgumentsPanel headers = new ArgumentsPanel("Headers");

    public AMQPRPCClientGui(){
        init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabelResource() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getStaticLabel() {
        return "AMQP RPCClient";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (!(element instanceof AMQPRPCClient)) return;
        AMQPRPCClient sampler = (AMQPRPCClient) element;

        persistent.setSelected(sampler.getPersistent());
        useTx.setSelected(sampler.getUseTx());

        messageRoutingKey.setText(sampler.getMessageRoutingKey());
        messageType.setText(sampler.getMessageType());
        replyToQueue.setText(sampler.getReplyToQueue());
        correlationId.setText(sampler.getCorrelationId());
        autoAck.setSelected(sampler.autoAck());
        message.setText(sampler.getMessage());
        configureHeaders(sampler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TestElement createTestElement() {
        AMQPRPCClient sampler = new AMQPRPCClient();
        modifyTestElement(sampler);
        return sampler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyTestElement(TestElement te) {
        AMQPRPCClient sampler = (AMQPRPCClient) te;
        sampler.clear();
        configureTestElement(sampler);

        super.modifyTestElement(sampler);

        sampler.setPersistent(persistent.isSelected());
        sampler.setUseTx(useTx.isSelected());

        sampler.setMessageRoutingKey(messageRoutingKey.getText());
        sampler.setMessage(message.getText());
        sampler.setMessageType(messageType.getText());
        sampler.setReplyToQueue(replyToQueue.getText());
        sampler.setCorrelationId(correlationId.getText());
        sampler.setAutoAck(autoAck.isSelected());
        sampler.setHeaders((Arguments) headers.createTestElement());
    }

    @Override
    protected void setMainPanel(JPanel panel){
        mainPanel = panel;
    }

    /*
     * Helper method to set up the GUI screen
     */
    @Override
    protected final void init() {
        super.init();
        persistent.setPreferredSize(new Dimension(100, 25));
        useTx.setPreferredSize(new Dimension(100, 25));
        messageRoutingKey.setPreferredSize(new Dimension(100, 25));
        messageType.setPreferredSize(new Dimension(100, 25));
        replyToQueue.setPreferredSize(new Dimension(100, 25));
        correlationId.setPreferredSize(new Dimension(100, 25));
        message.setPreferredSize(new Dimension(400, 150));

        mainPanel.add(persistent);
        mainPanel.add(useTx);
        mainPanel.add(messageRoutingKey);
        mainPanel.add(messageType);
        mainPanel.add(replyToQueue);
        mainPanel.add(correlationId);
        mainPanel.add(autoAck);
        mainPanel.add(headers);
        mainPanel.add(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearGui() {
        super.clearGui();
        persistent.setSelected(AMQPRPCClient.DEFAULT_PERSISTENT);
        useTx.setSelected(AMQPRPCClient.DEFAULT_USE_TX);
        messageRoutingKey.setText("");
        messageType.setText("");
        replyToQueue.setText("");
        correlationId.setText("");
        autoAck.setSelected(true);
        headers.clearGui();
        message.setText("");
    }

    private void configureHeaders(AMQPRPCClient sampler)
    {
        Arguments sampleHeaders = sampler.getHeaders();
        if (sampleHeaders != null) {
            headers.configure(sampleHeaders);
        } else {
            headers.clearGui();
        }
    }
}