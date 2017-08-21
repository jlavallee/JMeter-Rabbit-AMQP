include .mk

ANT_VERSION ?= 1.10.1
JMETER_HOME ?= ../jmeter

all: build install

build: vendor/ant/bin/ant
	vendor/ant/bin/ant

install:
	cp target/dist/JMeterAMQP.jar $(JMETER_HOME)/lib/ext/

vendor/ant/bin/ant:
	mkdir -p vendor
	cd vendor && curl -LO http://babyname.tips/mirrors/apache//ant/binaries/apache-ant-$(ANT_VERSION)-bin.tar.gz
	cd vendor && tar -xzf apache-ant-$(ANT_VERSION)-bin.tar.gz
	cd vendor && ln -s apache-ant-$(ANT_VERSION) ant

clean:
	vendor/ant/bin/ant clean
	rm -rf vendor

.mk:
	touch .mk
