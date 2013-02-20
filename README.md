# JMeter-Rabbit-AMQP #
======================

A [JMeter](http://jmeter.apache.org/) plugin to publish & consume messages from [RabbitMQ](http://www.rabbitmq.com/) or any [AMQP](http://www.amqp.org/) message broker.


JMeter Runtime Dependencies
---------------------------

Prior to building or installing this JMeter plugin, ensure that the RabbitMQ client library (rabbitmq-client.jar) is installed in JMeter's lib/ directory.


Build Dependencies
------------------

Before building the project, first create a lib/ directory underneath the base directory. 
One can either symlink or copy the following files from the JMeter lib/ directory into JMeter-Rabbit-AMQP's lib/ directory:
* rabbitmq-client.jar
* commons-codec-*.jar
* commons-collections-*.jar
* commons-httpclient-*.jar
* commons-io-*.jar
* commons-jexl-*.jar
* commons-lang-*.jar
* commons-logging-*.jar
* commons-net-*.jar
* jorphan.jar
* logkit.jar

In addition, you'll need to copy or symlink the following from JMeter's lib/ext directory:
* ApacheJMeter_core.jar


Building
--------

The project is built using Ant. To execute the build script, just execute:
    ant


Installing
----------

To install the plugin, build the project and copy the generated JMeterAMQP.jar file from target/dist to JMeter's lib/ directory.
