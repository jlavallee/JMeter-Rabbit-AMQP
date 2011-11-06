package com.zeroclue.jmeter.protocol.amqp.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.apache.jmeter.gui.util.FilePanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledRadio;
import org.apache.jorphan.gui.JLabeledTextArea;
import org.apache.jorphan.gui.JLabeledTextField;

import com.zeroclue.jmeter.protocol.amqp.AMQPPublisher;

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
public class AMQPPublisherGui extends AbstractSamplerGui {

    private static final long serialVersionUID = 1L;
    private static final String ALL_FILES = "*.*"; //$NON-NLS-1$

    private JLabeledTextField exchange = new JLabeledTextField("Exchange"); //$NON-NLS-1$
    private JLabeledTextField queue = new JLabeledTextField("Queue"); //$NON-NLS-1$
    private JLabeledTextField routingKey = new JLabeledTextField("Routing Key"); //$NON-NLS-1$
    private JLabeledTextField virtualHost = new JLabeledTextField("Virtual Host"); //$NON-NLS-1$
    private JLabeledTextField host = new JLabeledTextField("Host"); //$NON-NLS-1$
    private JLabeledTextField port = new JLabeledTextField("Port"); //$NON-NLS-1$
    private JLabeledTextField username = new JLabeledTextField("Username"); //$NON-NLS-1$
    private JLabeledTextField password = new JLabeledTextField("Password"); //$NON-NLS-1$
    private JLabeledTextField timeout = new JLabeledTextField("Timeout"); //$NON-NLS-1$

    /*
    private static final String[] CONFIG_CHOICES = {"File", "Static"};
    private final JLabeledRadio configChoice = new JLabeledRadio("Message Source", CONFIG_CHOICES); //$NON-NLS-1$
    private final FilePanel messageFile = new FilePanel("Filename", ALL_FILES); //$NON-NLS-1$
    */
    private JLabeledTextArea message = new JLabeledTextArea("Message Content"); //$NON-NLS-1$

    public AMQPPublisherGui() {
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
        return "AMQP Publisher";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (!(element instanceof AMQPPublisher)) return;
        AMQPPublisher sampler = (AMQPPublisher) element;

        exchange.setText(sampler.getExchange());
        queue.setText(sampler.getQueue());
        routingKey.setText(sampler.getRoutingKey());
        virtualHost.setText(sampler.getVirtualHost());
        host.setText(sampler.getHost());
        port.setText(sampler.getPort());
        timeout.setText(sampler.getTimeout());

        username.setText(sampler.getUsername());
        password.setText(sampler.getPassword());

        message.setText(sampler.getMessage());
    }

    /**
     * {@inheritDoc}
     */
    public TestElement createTestElement() {
        AMQPPublisher sampler = new AMQPPublisher();
        modifyTestElement(sampler);
        return sampler;
    }

    /**
     * {@inheritDoc}
     */
    public void modifyTestElement(TestElement te) {
        AMQPPublisher sampler = (AMQPPublisher) te;
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

        sampler.setMessage(message.getText());
    }

    /*
     * Helper method to set up the GUI screen
     */
    private void init() {
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

        /*
        configChoice.setLayout(new BoxLayout(configChoice, BoxLayout.X_AXIS));
        mainPanel.add(configChoice);
        mainPanel.add(messageFile);
        */
        mainPanel.add(message);
        Dimension pref = new Dimension(400, 150);
        message.setPreferredSize(pref);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearGui() {
        super.clearGui();
        exchange.setText("jmeterExchange");
        queue.setText("jmeterQueue");
        routingKey.setText("jmeterRoutingKey");
        virtualHost.setText("/");
        host.setText("localhost");
        port.setText(AMQPPublisher.DEFAULT_PORT_STRING);
        username.setText("guest");
        password.setText("guest");
        timeout.setText(AMQPPublisher.DEFAULT_TIMEOUT_STRING);
        //messageFile.setFilename("");
        message.setText(""); // $NON-NLS-1$
    }
}