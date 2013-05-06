package com.zeroclue.jmeter.protocol.amqp.gui;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.JLabeledChoice;
import org.apache.jorphan.gui.JLabeledTextField;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.zeroclue.jmeter.protocol.amqp.AMQPSampler;

public abstract class AMQPSamplerGui extends AbstractSamplerGui {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    protected JLabeledTextField exchange = new JLabeledTextField("Exchange");
    protected JLabeledTextField queue = new JLabeledTextField("Queue");
    protected JLabeledTextField routingKey = new JLabeledTextField("Routing Key");
    protected JLabeledTextField virtualHost = new JLabeledTextField("Virtual Host");
    protected JLabeledTextField messageTTL = new JLabeledTextField("Message TTL");
    protected JLabeledChoice exchangeType = new JLabeledChoice("Exchange Type", new String[]{ "direct", "topic", "headers", "fanout"});
    private final JCheckBox queueDurable = new JCheckBox("Durable?", true);
    private final JCheckBox queueExclusive = new JCheckBox("Exclusive", true);
    private final JCheckBox queueAutoDelete = new JCheckBox("Auto Delete?", true);

    protected JLabeledTextField host = new JLabeledTextField("Host");
    protected JLabeledTextField port = new JLabeledTextField("Port");
    protected JLabeledTextField timeout = new JLabeledTextField("Timeout");
    protected JLabeledTextField username = new JLabeledTextField("Username");
    protected JLabeledTextField password = new JLabeledTextField("Password");

    private final JLabeledTextField iterations = new JLabeledTextField("Number of samples to Aggregate");



    protected abstract void setMainPanel(JPanel panel);

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (!(element instanceof AMQPSampler)) return;
        AMQPSampler sampler = (AMQPSampler) element;

        exchange.setText(sampler.getExchange());
        exchangeType.setText(sampler.getExchangeType());
        queue.setText(sampler.getQueue());
        routingKey.setText(sampler.getRoutingKey());
        virtualHost.setText(sampler.getVirtualHost());
        messageTTL.setText(sampler.getMessageTTL());
        queueDurable.setSelected(sampler.queueDurable());
        queueExclusive.setSelected(sampler.queueExclusive());
        queueAutoDelete.setSelected(sampler.queueAutoDelete());

        timeout.setText(sampler.getTimeout());
        iterations.setText(sampler.getIterations());

        host.setText(sampler.getHost());
        port.setText(sampler.getPort());
        username.setText(sampler.getUsername());
        password.setText(sampler.getPassword());
        log.info("AMQPSamplerGui.configure() called");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearGui() {
        exchange.setText("jmeterExchange");
        queue.setText("jmeterQueue");
        routingKey.setText("jmeterRoutingKey");
        virtualHost.setText("/");
        messageTTL.setText("");
        exchangeType.setText("direct");
        queueDurable.setSelected(true);
        queueExclusive.setSelected(false);
        queueAutoDelete.setSelected(false);

        timeout.setText(AMQPSampler.DEFAULT_TIMEOUT_STRING);
        iterations.setText(AMQPSampler.DEFAULT_ITERATIONS_STRING);

        host.setText("localhost");
        port.setText(AMQPSampler.DEFAULT_PORT_STRING);
        username.setText("guest");
        password.setText("guest");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyTestElement(TestElement element) {
        AMQPSampler sampler = (AMQPSampler) element;
        sampler.clear();
        configureTestElement(sampler);

        sampler.setExchange(exchange.getText());
        sampler.setQueue(queue.getText());
        sampler.setRoutingKey(routingKey.getText());
        sampler.setVirtualHost(virtualHost.getText());
        sampler.setMessageTTL(messageTTL.getText());
        sampler.setExchangeType(exchangeType.getText());
        sampler.setQueueDurable(queueDurable.isSelected());
        sampler.setQueueExclusive(queueExclusive.isSelected());
        sampler.setQueueAutoDelete(queueAutoDelete.isSelected());

        sampler.setTimeout(timeout.getText());
        sampler.setIterations(iterations.getText());

        sampler.setHost(host.getText());
        sampler.setPort(port.getText());
        sampler.setUsername(username.getText());
        sampler.setPassword(password.getText());
        log.info("AMQPSamplerGui.modifyTestElement() called, set user/pass to " + username.getText() + "/" + password.getText() + " on sampler " + sampler);
    }

    protected void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH); // Add the standard title

        JPanel mainPanel = new VerticalPanel();

        mainPanel.add(makeCommonPanel());

        iterations.setPreferredSize(new Dimension(50,25));
        mainPanel.add(iterations);

        add(mainPanel);

        setMainPanel(mainPanel);
    }

    private Component makeCommonPanel() {
        GridBagConstraints gridBagConstraints, gridBagConstraintsCommon;

        gridBagConstraintsCommon = new GridBagConstraints();
        gridBagConstraintsCommon.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraintsCommon.anchor = GridBagConstraints.WEST;
        gridBagConstraintsCommon.weightx = 0.5;

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;

        JPanel commonPanel = new JPanel(new GridBagLayout());

        JPanel exchangeSettings = new JPanel(new GridBagLayout());
        exchangeSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Exchange"));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        exchangeSettings.add(exchange, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        exchangeSettings.add(exchangeType, gridBagConstraints);

        JPanel queueSettings = new JPanel(new GridBagLayout());
        queueSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Queue"));


        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        queueSettings.add(queue, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        queueSettings.add(routingKey, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        queueSettings.add(messageTTL, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        queueSettings.add(queueDurable, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        queueSettings.add(queueExclusive, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        queueSettings.add(queueAutoDelete, gridBagConstraints);

        gridBagConstraintsCommon.gridx = 0;
        gridBagConstraintsCommon.gridy = 0;

        JPanel exchangeQueueSettings = new VerticalPanel();
        exchangeQueueSettings.add(exchangeSettings);
        exchangeQueueSettings.add(queueSettings);

        commonPanel.add(exchangeQueueSettings, gridBagConstraintsCommon);


        JPanel serverSettings = new JPanel(new GridBagLayout());
        serverSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Connection"));

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        serverSettings.add(virtualHost, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        serverSettings.add(host, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        serverSettings.add(port, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        serverSettings.add(username, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        serverSettings.add(password, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        serverSettings.add(timeout, gridBagConstraints);


        gridBagConstraintsCommon.gridx = 1;
        gridBagConstraintsCommon.gridy = 0;

        commonPanel.add(serverSettings, gridBagConstraintsCommon);

        return commonPanel;
    }

}
