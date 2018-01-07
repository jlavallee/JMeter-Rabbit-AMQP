#!/bin/sh

# jmeter-rabbitmq-setup.sh From https://github.com/wilsonmar/JMeter-Rabbit-AMQP
# This script bootstraps a OSX laptop for development
# to a point where we can run Ansible on localhost. It:
#  1. Installs:
#    - xcode
#    - homebrew, then via brew:
#    - java JDK 
#
# It begins by asking for your sudo password:

fancy_echo() {
  local fmt="$1"; shift
  # shellcheck disable=SC2059
  printf "\n>>> $fmt\n" "$@"
}

fancy_echo "Boostrapping ..."

#trap 'ret=$?; test $ret -ne 0 && printf "failed\n\n" >&2; exit $ret' EXIT
#et -e

# Here we go.. ask for the administrator password upfront and run a
# keep-alive to update existing `sudo` time stamp until script has finished
# sudo -v
# while true; do sudo -n true; sleep 60; kill -0 "$$" || exit; done 2>/dev/null &

# Ensure Apple's command line tools are installed
if ! command -v cc >/dev/null; then
  fancy_echo "Installing xcode. It's needed by Homebrew ..."
  xcode-select --install 
else
  fancy_echo "Xcode already installed. Skipping."
fi

if ! command -v brew >/dev/null; then
  fancy_echo "Installing Homebrew for brew commands ..."
  ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)" </dev/null
else
  fancy_echo "Homebrew already installed. Skipping."
fi

if ! command -v java >/dev/null; then
  fancy_echo "Installing JDK ..."
   brew cask info java8
   brew cask install java8
   javac -version  
else
  fancy_echo "JDK already installed. Skipping."
   javac -version  
fi

if ! command -v jmeter >/dev/null; then
  fancy_echo "Installing jmeter ..."
  brew install jmeter
   export JMETER_HOME="/usr/local/Cellar/jmeter/3.3"
   echo $JMETER_HOME
   ls $JMETER_HOME
  fancy_echo "jmeter already installed. Skipping."
fi

# FILE="target/dist/JMeterAMQP.jar"
if [ -f $FILE ]; then
  fancy_echo "Cloning JMeter-Rabbit-AMQP repo ..."
   echo $FILE
   git clone https://github.com/wilsonmar/JMeter-Rabbit-AMQP --depth=1
   cd JMeter-Rabbit-AMQPelse
else
  fancy_echo "JMeter-Rabbit-AMQP repo already exists ..."
fi

if ! command -v tree >/dev/null; then
  fancy_echo "Installing tree utlity ..."
  brew install tree
else
  fancy_echo "tree already installed. Skipping."
fi
  fancy_echo "Tree ...."
   pwd
   tree -L 1

# cd ~/gits

if ! command -v ant >/dev/null; then
  fancy_echo "Installing ant utlity ..."
  brew install ant
else
  fancy_echo "ant already installed. Skipping."
fi
  fancy_echo "ant running..."
  ant

# If file exists:
FILE="target/dist/JMeterAMQP.jar"
if [ -f $FILE ]; then
  fancy_echo "Installing JMeterAMPQ.jar ..."
   echo $FILE
   cp $FILE  $JMETER_HOME/libexec/lib/ext
   ls $JMETER_HOME/libexec/lib/ext -al
else
   echo "File '$FILE' not found. Aborting..."
   exit
fi

  fancy_echo "ivy running..."
   java -jar ivy.jar -dependency com.rabbitmq amqp-client 3.6.1 \
      -retrieve "$JMETER_HOME/lab/[artifact](-[classifier]).[ext]"

if ! command -v rabbitmq-server >/dev/null; then
  fancy_echo "Installing rabbitmq-server locally as server under test ..."
  brew install rabbitmq
else
  fancy_echo "rabbitmq-server already installed. Skipping."
fi

if [[ ":$PATH:" == *":$HOME/usr/local/sbin:"* ]]; then
  fancy_echo "rabbitmq in path already. Skipping."
else
  fancy_echo "Add path of rabbitmq ..."
   export PATH=$PATH:/usr/local/sbin
fi
fancy_echo "Starting Rabbitmq server ..."
   rabbitmq-server

#fancy_echo "Starting JMeter..."
#    jmeter ???

# watch run at http://localhost:15672

fancy_echo "Done."
