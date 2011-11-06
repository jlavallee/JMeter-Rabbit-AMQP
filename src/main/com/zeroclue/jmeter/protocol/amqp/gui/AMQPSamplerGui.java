package com.zeroclue.jmeter.protocol.amqp.gui;

import java.awt.BorderLayout;

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
        host.setText(sampler.getHost());
        port.setText(sampler.getPort());
        timeout.setText(sampler.getTimeout());

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
        host.setText("localhost");
        port.setText(AMQPSampler.DEFAULT_PORT_STRING);
        timeout.setText(AMQPSampler.DEFAULT_TIMEOUT_STRING);
        
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
        sampler.setHost(host.getText());
        sampler.setPort(port.getText());
        sampler.setTimeout(timeout.getText());

        sampler.setUsername(username.getText());
        sampler.setPassword(password.getText());
    }

    protected void init() {
        
        // Standard setup
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH); // Add the standard title

        // Specific setup
        JPanel mainPanel = new VerticalPanel();
        add(mainPanel, BorderLayout.CENTER);
        
        mainPanel.add(exchange);
        mainPanel.add(queue);
        mainPanel.add(routingKey);
        mainPanel.add(virtualHost);
        mainPanel.add(host);
        mainPanel.add(port);
        mainPanel.add(username);
        mainPanel.add(password);
        
        setMainPanel(mainPanel);
    }
    
}
