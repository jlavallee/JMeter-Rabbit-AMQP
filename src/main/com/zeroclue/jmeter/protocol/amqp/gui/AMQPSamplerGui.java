package com.zeroclue.jmeter.protocol.amqp.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledTextField;

import com.zeroclue.jmeter.protocol.amqp.AMQPSampler;

public abstract class AMQPSamplerGui extends AbstractSamplerGui {

    private static final long serialVersionUID = 1L;

    protected JLabeledTextField exchange = new JLabeledTextField("Exchange"); //$NON-NLS-1$
    protected JLabeledTextField queue = new JLabeledTextField("Queue"); //$NON-NLS-1$
    protected JLabeledTextField routingKey = new JLabeledTextField("Routing Key"); //$NON-NLS-1$
    protected JLabeledTextField virtualHost = new JLabeledTextField("Virtual Host"); //$NON-NLS-1$
    protected JLabeledTextField messageTTL = new JLabeledTextField("Message TTL"); //$NON-NLS-1$

    protected JLabeledTextField host = new JLabeledTextField("Host"); //$NON-NLS-1$
    protected JLabeledTextField port = new JLabeledTextField("Port"); //$NON-NLS-1$
    protected JLabeledTextField timeout = new JLabeledTextField("Timeout"); //$NON-NLS-1$
    protected JLabeledTextField username = new JLabeledTextField("Username"); //$NON-NLS-1$
    protected JLabeledTextField password = new JLabeledTextField("Password"); //$NON-NLS-1$

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
        queue.setText(sampler.getQueue());
        routingKey.setText(sampler.getRoutingKey());
        virtualHost.setText(sampler.getVirtualHost());
        messageTTL.setText(sampler.getMessageTTL());

        timeout.setText(sampler.getTimeout());

        host.setText(sampler.getHost());
        port.setText(sampler.getPort());
        username.setText(sampler.getUsername());
        password.setText(sampler.getPassword());
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

        timeout.setText(AMQPSampler.DEFAULT_TIMEOUT_STRING);

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


        sampler.setTimeout(timeout.getText());

        sampler.setHost(host.getText());
        sampler.setPort(port.getText());
        sampler.setUsername(username.getText());
        sampler.setPassword(password.getText());
    }

    protected void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH); // Add the standard title

        JPanel mainPanel = new VerticalPanel();

        mainPanel.add(makeCommonPanel());

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

        JPanel queueSettings = new JPanel(new GridBagLayout());
        queueSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Queue"));

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        queueSettings.add(exchange, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        queueSettings.add(queue, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        queueSettings.add(routingKey, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        queueSettings.add(virtualHost, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        queueSettings.add(messageTTL, gridBagConstraints);

        gridBagConstraintsCommon.gridx = 0;
        gridBagConstraintsCommon.gridy = 0;
        commonPanel.add(queueSettings, gridBagConstraintsCommon);

        JPanel serverSettings = new JPanel(new GridBagLayout());
        serverSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Server"));

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        serverSettings.add(host, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        serverSettings.add(port, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        serverSettings.add(username, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        serverSettings.add(password, gridBagConstraints);

        gridBagConstraintsCommon.gridx = 1;
        gridBagConstraintsCommon.gridy = 0;

        commonPanel.add(serverSettings, gridBagConstraintsCommon);

        JPanel clientSettings = new JPanel(new GridBagLayout());
        clientSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Client"));

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        clientSettings.add(timeout, gridBagConstraints);

        gridBagConstraintsCommon.gridx = 0;
        gridBagConstraintsCommon.gridy = 1;

        commonPanel.add(clientSettings, gridBagConstraintsCommon);
        return commonPanel;
    }

}
