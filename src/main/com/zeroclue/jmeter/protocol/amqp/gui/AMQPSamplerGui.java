package com.zeroclue.jmeter.protocol.amqp.gui;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.JLabeledChoice;
import org.apache.jorphan.gui.JLabeledTextField;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.zeroclue.jmeter.protocol.amqp.AMQPPublisher;
import com.zeroclue.jmeter.protocol.amqp.AMQPSampler;

public abstract class AMQPSamplerGui extends AbstractSamplerGui {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggingManager.getLoggerForClass();

	protected JLabeledTextField exchange = new JLabeledTextField("Exchange");
	private final JCheckBox exchangeRedeclare = new JCheckBox("Redeclare?", AMQPSampler.DEFAULT_EXCHANGE_REDECLARE);
	protected final JCheckBox exchangeAutoDelete = new JCheckBox("Auto delete?", AMQPSampler.DEFAULT_EXCHANGE_AUTO_DELETE);
	protected final JCheckBox exchangeInternal = new JCheckBox("Internal?", AMQPSampler.DEFAULT_EXCHANGE_INTERNAL);
	protected JLabeledTextField queue = new JLabeledTextField("Queue");
	protected JLabeledTextField routingKey = new JLabeledTextField("Routing Key");
	protected JLabeledTextField virtualHost = new JLabeledTextField("Virtual Host");
	protected JLabeledTextField messageTTL = new JLabeledTextField("Message TTL");
	protected JLabeledTextField messageExpires = new JLabeledTextField("Expires");
	protected JLabeledChoice exchangeType = new JLabeledChoice("Exchange Type", new String[] { "direct", "topic", "headers",
			"fanout" });
	private final JCheckBox exchangeDurable = new JCheckBox("Durable?", AMQPSampler.DEFAULT_EXCHANGE_DURABLE);
	private final JCheckBox queueDurable = new JCheckBox("Durable?", true);
	private final JCheckBox queueRedeclare = new JCheckBox("Redeclare?", AMQPSampler.DEFAULT_QUEUE_REDECLARE);
	private final JCheckBox queueExclusive = new JCheckBox("Exclusive", true);
	private final JCheckBox queueAutoDelete = new JCheckBox("Auto Delete?", true);

	protected JLabeledTextField host = new JLabeledTextField("Host");
	protected JLabeledTextField port = new JLabeledTextField("Port");
	protected JLabeledTextField timeout = new JLabeledTextField("Timeout");
	protected JLabeledTextField username = new JLabeledTextField("Username");
	protected JLabeledTextField password = new JLabeledTextField("Password");
	private final JCheckBox SSL = new JCheckBox("SSL?", false);

	private final JLabeledTextField iterations = new JLabeledTextField("Number of samples to Aggregate");

	protected ArgumentsPanel exchangeParameters = new ArgumentsPanel("Exchange parameters");
	protected ArgumentsPanel queueParameters = new ArgumentsPanel("Queue parameters");

	protected abstract void setMainPanel(JPanel panel);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(TestElement element) {
		super.configure(element);
		if (!(element instanceof AMQPSampler))
			return;
		AMQPSampler sampler = (AMQPSampler) element;

		exchange.setText(sampler.getExchange());
		exchangeType.setText(sampler.getExchangeType());
		exchangeDurable.setSelected(sampler.getExchangeDurable());
		exchangeRedeclare.setSelected(sampler.getExchangeRedeclare());
		queue.setText(sampler.getQueue());
		routingKey.setText(sampler.getRoutingKey());
		virtualHost.setText(sampler.getVirtualHost());
		messageTTL.setText(sampler.getMessageTTL());
		messageExpires.setText(sampler.getMessageExpires());
		queueDurable.setSelected(sampler.queueDurable());
		queueExclusive.setSelected(sampler.queueExclusive());
		queueAutoDelete.setSelected(sampler.queueAutoDelete());
		queueRedeclare.setSelected(sampler.getQueueRedeclare());
		exchangeAutoDelete.setSelected(sampler.getAutoDelete());
		exchangeInternal.setSelected(sampler.getInternal());

		configureExchangeParameters(sampler);
		configureQueueParameters(sampler);

		timeout.setText(sampler.getTimeout());
		iterations.setText(sampler.getIterations());

		host.setText(sampler.getHost());
		port.setText(sampler.getPort());
		username.setText(sampler.getUsername());
		password.setText(sampler.getPassword());
		SSL.setSelected(sampler.connectionSSL());
		log.info("AMQPSamplerGui.configure() called");
	}

	private void configureExchangeParameters(AMQPSampler sampler) {
		Arguments sampleHeaders = sampler.getExchangeParameters();
		if (sampleHeaders != null) {
			exchangeParameters.configure(sampleHeaders);
		} else {
			exchangeParameters.clearGui();
		}
	}

	private void configureQueueParameters(AMQPSampler sampler) {
		Arguments sampleHeaders = sampler.getQueueParameters();
		if (sampleHeaders != null) {
			queueParameters.configure(sampleHeaders);
		} else {
			queueParameters.clearGui();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearGui() {
		exchange.setText("jmeterExchange");
		queue.setText("jmeterQueue");
		exchangeDurable.setSelected(AMQPSampler.DEFAULT_EXCHANGE_DURABLE);
		exchangeRedeclare.setSelected(AMQPSampler.DEFAULT_EXCHANGE_REDECLARE);
		routingKey.setText("jmeterRoutingKey");
		virtualHost.setText("/");
		messageTTL.setText("");
		messageExpires.setText("");
		exchangeType.setText("direct");
		queueDurable.setSelected(true);
		queueExclusive.setSelected(false);
		queueAutoDelete.setSelected(false);
		exchangeAutoDelete.setSelected(false);
		exchangeInternal.setSelected(false);
		queueRedeclare.setSelected(AMQPSampler.DEFAULT_QUEUE_REDECLARE);

		exchangeParameters.clearGui();
		queueParameters.clearGui();

		timeout.setText(AMQPSampler.DEFAULT_TIMEOUT_STRING);
		iterations.setText(AMQPSampler.DEFAULT_ITERATIONS_STRING);

		host.setText("localhost");
		port.setText(AMQPSampler.DEFAULT_PORT_STRING);
		username.setText("guest");
		password.setText("guest");
		SSL.setSelected(false);
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
		sampler.setExchangeDurable(exchangeDurable.isSelected());
		sampler.setExchangeRedeclare(exchangeRedeclare.isSelected());
		sampler.setExchangeAutoDelete(exchangeAutoDelete.isSelected());
		sampler.setInternal(exchangeInternal.isSelected());
		sampler.setQueue(queue.getText());
		sampler.setRoutingKey(routingKey.getText());
		sampler.setVirtualHost(virtualHost.getText());
		sampler.setMessageTTL(messageTTL.getText());
		sampler.setMessageExpires(messageExpires.getText());
		sampler.setExchangeType(exchangeType.getText());
		sampler.setQueueDurable(queueDurable.isSelected());
		sampler.setQueueExclusive(queueExclusive.isSelected());
		sampler.setQueueAutoDelete(queueAutoDelete.isSelected());
		sampler.setQueueRedeclare(queueRedeclare.isSelected());

		sampler.setExchangeParameters((Arguments) exchangeParameters.createTestElement());
		sampler.setQueueParameters((Arguments) queueParameters.createTestElement());

		sampler.setTimeout(timeout.getText());
		sampler.setIterations(iterations.getText());

		sampler.setHost(host.getText());
		sampler.setPort(port.getText());
		sampler.setUsername(username.getText());
		sampler.setPassword(password.getText());
		sampler.setConnectionSSL(SSL.isSelected());
		log.info("AMQPSamplerGui.modifyTestElement() called, set user/pass to " + username.getText() + "/" + password.getText()
				+ " on sampler " + sampler);
	}

	protected void init() {
		setLayout(new BorderLayout(0, 5));
		setBorder(makeBorder());
		add(makeTitlePanel(), BorderLayout.NORTH); // Add the standard title

		JPanel mainPanel = new VerticalPanel();

		mainPanel.add(makeCommonPanel());

		iterations.setPreferredSize(new Dimension(50, 25));
		mainPanel.add(iterations);
//		mainPanel.add(exchangeAutoDelete);
//		mainPanel.add(exchangeInternal);
//		mainPanel.add(exchangeParameters);
//		mainPanel.add(queueParameters);
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

		// =====================Exchange settings==========================
		JPanel exchangeSettings = new JPanel(new GridBagLayout());
		exchangeSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Exchange"));

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		exchangeSettings.add(exchange, gridBagConstraints);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		exchangeSettings.add(exchangeType, gridBagConstraints);

		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		exchangeSettings.add(exchangeDurable, gridBagConstraints);

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		exchangeSettings.add(exchangeRedeclare, gridBagConstraints);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		exchangeSettings.add(exchangeAutoDelete, gridBagConstraints);

		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		exchangeSettings.add(exchangeInternal, gridBagConstraints);

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		exchangeSettings.add(exchangeParameters, gridBagConstraints);
		// ====================================================

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

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		queueSettings.add(messageExpires, gridBagConstraints);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		queueSettings.add(queueDurable, gridBagConstraints);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		queueSettings.add(queueExclusive, gridBagConstraints);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		queueSettings.add(queueAutoDelete, gridBagConstraints);

		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		queueSettings.add(queueRedeclare, gridBagConstraints);

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		queueSettings.add(queueParameters, gridBagConstraints);

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

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		serverSettings.add(SSL, gridBagConstraints);

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
