package com.zeroclue.jmeter.protocol.amqp.gui;

import java.awt.Dimension;

import javax.swing.JPanel;

import org.apache.jmeter.testelement.TestElement;
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
public class AMQPPublisherGui extends AMQPSamplerGui {

    private static final long serialVersionUID = 1L;
    
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

        timeout.setText(sampler.getTimeout());
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
        
        super.modifyTestElement(sampler);

        sampler.setTimeout(timeout.getText());
        sampler.setMessage(message.getText());
    }
    
    private JPanel mainPanel;
    protected void setMainPanel(JPanel panel){
        mainPanel = panel;
    }

    /*
     * Helper method to set up the GUI screen
     */
    protected void init() {
        super.init();

        /*
        configChoice.setLayout(new BoxLayout(configChoice, BoxLayout.X_AXIS));
        mainPanel.add(configChoice);
        mainPanel.add(messageFile);
        */
        mainPanel.add(timeout);
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
        timeout.setText(AMQPPublisher.DEFAULT_TIMEOUT_STRING);
        //messageFile.setFilename("");
        message.setText(""); // $NON-NLS-1$
    }
}