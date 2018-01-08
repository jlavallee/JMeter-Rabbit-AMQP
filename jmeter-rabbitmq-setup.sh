#!/bin/sh

# jmeter-rabbitmq-setup.sh From https://github.com/wilsonmar/JMeter-Rabbit-AMQP
# This script bootstraps a OSX laptop for development
# See https://wilsonmar.github.io/
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

trap 'ret=$?; test $ret -ne 0 && printf "failed\n\n" >&2; exit $ret' EXIT
set -e

fancy_echo "Starting jmeter-rabbitmq-setup.sh ................................."
#  clear
  sw_vers


# Here we go.. ask for the administrator password upfront and run a
# keep-alive to update existing `sudo` time stamp until script has finished
# sudo -v
# while true; do sudo -n true; sleep 60; kill -0 "$$" || exit; done 2>/dev/null &

# Ensure Apple's command line tools are installed
if ! command -v cc >/dev/null; then
  fancy_echo "Installing xcode. It's needed by Homebrew ..."
  xcode-select --install 
else
  fancy_echo "Xcode already installed. Skipping install."
fi
  xcodebuild -version


if ! command -v brew >/dev/null; then
  fancy_echo "Installing Homebrew for brew commands ..."
  ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)" </dev/null
else
  fancy_echo "Homebrew already installed. Skipping install."
  brew --version  # Homebrew 1.4.2
fi


if ! command -v java >/dev/null; then
  fancy_echo "Installing Java JDK ..."
   brew cask info java8
   brew cask install java8
   javac -version  
else
  fancy_echo "Java JDK already installed. Skipping install."
   javac -version  
fi


export JMETER_HOME="/usr/local/Cellar/jmeter/3.3"
if [ -d $JMETER_HOME ]; then
  fancy_echo "$JMETER_HOME already installed. Skipping install."
else
  fancy_echo "Installing jmeter to $JMETER_HOME ..."
  brew install jmeter
fi
   ls $JMETER_HOME
   # jmeter -v  # with that big ASCII art banner.


REPO1="JMeter-Rabbit-AMQP"
if [ -d $REPO1 ]; then
  fancy_echo "Repo $REPO1 folder exists, so deleting..."
  rm -rf $REPO1
else
  fancy_echo "Repo $REPO1 folder does not exist ..."
fi
  fancy_echo "Repo $REPO1 being cloned ..."
   git clone https://github.com/wilsonmar/JMeter-Rabbit-AMQP --depth=1
   cd $REPO1
   pwd
   #tree


if ! command -v tree >/dev/null; then
  fancy_echo "Installing tree utility missing in MacOS ..."
  brew install tree
else
  fancy_echo "tree already installed. Skipping install."
fi
   tree -L 1


# cd ~/gits

if ! command -v ant >/dev/null; then
  fancy_echo "Installing ant utlity ..."
  brew install ant
  ant -v
else
  fancy_echo "ant already installed. Skipping install."
  ant -v
fi
  fancy_echo "ant running to process ant.xml ..."
  ant


FILE="target/dist/JMeterAMQP.jar"
if [ -f $FILE ]; then  # file exists within folder $REPO1
  fancy_echo "$FILE was created ..."
   ls -al    $FILE                        | grep JMeterAMQP.jar
  fancy_echo "Removing previous within JMETER_HOME ..."
   ls -al    $JMETER_HOME/libexec/lib/ext | grep JMeterAMQP.jar
   rm        $JMETER_HOME/libexec/lib/ext/JMeterAMQP.jar
  fancy_echo "Copying in from $FILE ..."
   cp $FILE  $JMETER_HOME/libexec/lib/ext
   ls -al    $JMETER_HOME/libexec/lib/ext | grep JMeterAMQP.jar
else
   fancy_echo "File '$FILE' not found. Aborting..."
   exit
fi


pwd


  fancy_echo "ivy running in ivy folder ..."
  java -jar ivy/ivy.jar -dependency com.rabbitmq amqp-client 3.6.1 \
      -retrieve "$JMETER_HOME/lab/[artifact](-[classifier]).[ext]"

if ! command -v rabbitmq-server >/dev/null; then
  fancy_echo "Installing rabbitmq-server locally as server under test ..."
  brew install rabbitmq
else
  fancy_echo "rabbitmq-server already installed. Skipping install."
fi


if [[ ":$PATH:" == *":$HOME/usr/local/sbin:"* ]]; then
  fancy_echo "rabbitmq in path already. Skipping install."
else
  fancy_echo "Add path of rabbitmq /usr/local/sbin ..."
   export PATH=$PATH:/usr/local/sbin
fi
  fancy_echo "Starting Rabbitmq server in background using nohup ..."
   nohup rabbitmq-server &>/dev/null &
   jobs
   ps
   open http://localhost:15672  # default port (open is Mac only command)


   fancy_echo "Starting JMeter in background to run test ..."
#   nohup "./jmeter.sh -n -t $REPO1/bin/GamesSubscriber.jmx -l result.jtl" > /dev/null 2>&1 &


fancy_echo "Done."
