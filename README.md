# JMeter AMQP Plugin #

![License](https://img.shields.io/github/workflow/status/verhyppo/jmeter-AMQP-plugin/tagged-release?style=for-the-badge)
![Issues](https://img.shields.io/github/issues/verhyppo/jmeter-AMQP-plugin?style=for-the-badge)
![Downloads](https://img.shields.io/github/downloads/verhyppo/jmeter-amqp-plugin/total?style=for-the-badge)
![Release](https://img.shields.io/github/v/release/verhyppo/jmeter-AMQP-plugin?style=for-the-badge)

A [JMeter](http://jmeter.apache.org/) plugin to publish & consume messages from [RabbitMQ](http://www.rabbitmq.com/) or any [AMQP](http://www.amqp.org/) message broker.


- [JMeter AMQP Plugin](#jmeter-amqp-plugin)
  - [1.1. Installation](#11-installation)
  - [1.2. Build](#12-build)
  - [1.3. Contributing](#13-contributing)
  - [1.4. License](#14-license)

## 1.1. Installation

All releases and their changelog can be found under [releases](https://github.com/verhyppo/jmeter-AMQP-plugin/releases).

All releases released as `Development Build` are considered `pre releases` and are considered unstable.

Releases tagged with a `v*` version are the ones for which bugs are fixed with higher priority.

In order to use the `jar` file as JMeter Plugin you have to add to JMeter `${JMETER_HOME}/lib` folder the following artifacts:

* the release got from releases as mentioned above
* [AMQP](https://mvnrepository.com/artifact/com.rabbitmq/amqp-client) client library

Once done so, you will find two new sampler in JMeter called `AMQPConsumer` and `AMQPPublisher`, then you can start using them as usual.


## 1.2. Build

Build and Dev dependencies are managed via [Maven](https://maven.apache.org).

In order to build the artifacts locally, just issue from the root folder of the project
```
mvn clean package
```

## 1.3. Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## 1.4. License
[Apache 2.0](LICENSE)