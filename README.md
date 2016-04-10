# JMeter-Rabbit-AMQP #
======================

A [JMeter](http://jmeter.apache.org/) plugin to publish & consume messages from [RabbitMQ](http://www.rabbitmq.com/) or any [AMQP](http://www.amqp.org/) message broker.


JMeter Runtime Dependencies
---------------------------

Prior to building or installing this JMeter plugin, ensure that the RabbitMQ client library (amqp-client-3.x.x.jar) is installed in JMeter's lib/ directory.


Build Dependencies
------------------

Build dependencies are managed by Maven. JARs should automagically be downloaded by Maven as part of the build process.


Building
--------

The project is built using Maven. To execute the build script, just execute:
    mvn clean compile assembly:single


Installing
----------

To install the plugin, build the project and copy the generated jmeter-rabbit-amqp-1.0.0-jar-with-dependencies.jar file from target to JMeter's lib/ext/ directory.
