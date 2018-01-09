#!/bin/sh

# jmeter-rabbitmq-setup.sh From https://github.com/wilsonmar/JMeter-Rabbit-AMQP
# This script bootstraps a OSX laptop for development.
# Resources are created new each run (after deleting leftovers from previous run)
# Steps here are explained in https://wilsonmar.github.io/jmeter-install/
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
function pause(){
   read -p "$*"
}


trap 'ret=$?; test $ret -ne 0 && printf "failed\n\n" >&2; exit $ret' EXIT
set -e

BEGIN=`date +%s`
fancy_echo "Starting jmeter-rabbitmq-setup.sh ................................."
#  clear
  sw_vers
    # ProductName:	Mac OS X
    # ProductVersion:	10.11.6
    # BuildVersion:	15G18013


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
    # Xcode 7.3.1
    # Build version 7D1014

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
   # javac 1.8.0_152


export JMETER_HOME="/usr/local/Cellar/jmeter/3.3"
if [ -d $JMETER_HOME ]; then
  fancy_echo "$JMETER_HOME already installed. Skipping install."
else
  fancy_echo "Installing jmeter to $JMETER_HOME ..."
  brew install jmeter
fi
   ls $JMETER_HOME/libexec
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
   pwd
   tree -L 1



FILE="meter-plugins-manager-0.18.jar"
FILE_PATH="$JMETER_HOME/libexec/lib/ext/$FILE"  # TODO: Check if version has changed since Jan 4, 2018.
if [ -f $FILE_PATH ]; then  # file exists within folder 
   fancy_echo "$FILE already installed. Skipping install."
   ls -al    $JMETER_HOME/libexec/lib/ext | grep $FILE
else
   fancy_echo "Downloading $FILE to $FOLDER ..."
   # From https://jmeter-plugins.org/wiki/StandardSet/
   curl -O http://jmeter-plugins.org/downloads/file/$FILE
   ls -al    $FILE
   fancy_echo "Overwriting $FILE_PATH ..."
   yes | cp -rf $FILE  $FILE_PATH 
   ls -al    $JMETER_HOME/libexec/lib/ext | grep $FILE
fi


FILE="JMeterPlugins-Extras-1.2.1.zip"  # TODO: Check if version has changed since Jan 4, 2018.
FILE_PATH="$JMETER_HOME/libexec/lib/ext/$FILE"
if [ -f $FILE_PATH ]; then  # file exists within folder 
   fancy_echo "$FILE already installed. Skipping install."
   ls -al    $JMETER_HOME/libexec/lib/ext | grep $FILE
else
   fancy_echo "Downloading $FILE to $FOLDER ..."
   # From https://jmeter-plugins.org/wiki/Extras
   curl -O http://jmeter-plugins.org/downloads/file/JMeterPlugins-Extras-1.2.1.zip
   ls -al    $FILE
   fancy_echo "Overwriting $FILE_PATH ..."
   yes | cp -rf $FILE  $FILE_PATH 
   ls -al    $FILE_PATH
fi



if ! command -v ant >/dev/null; then
  fancy_echo "Installing ant utlity ..."
  brew install ant
  ant -v
else
  fancy_echo "ant already installed. Skipping install."
  ant -v
fi
  fancy_echo "ant run to process ant.xml ..."
  ant
  # Ant can pick up the Test.jmx file, execute it, and generate an easily-readable HTML report.


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



  fancy_echo "ivy java program running in ivy folder ..."
  pwd
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

   #open http://localhost:15672  # 5672 default port (open is Mac only command)
#pause 'Press [Enter] key to continue...'

export JMETER_FILE="rabbitmq_test"
   fancy_echo "Starting JMeter in background for $JMETER_FILE ..."
   pwd
   ls -al $REPO1/examples/rabbitmq_test.jmx 
   $JMETER_HOME/libexec/bin/jmeter.sh -n -t $REPO1/examples/rabbitmq_test.jmx -l rabbitmq_test.jtl
#   nohup "./jmeter.sh -n -t $REPO1/examples/rabbitmq_test.jmx -l result.jtl" > /dev/null 2>&1 &
# -n for NON-GUI mode jmeter -n -t [jmx file] -l [results file] -e -o [Path to output folder]

   fancy_echo "Process rabbitmq_test.jtl ..."
#   subl rabbitmq_test.jtl


END=`date +%s`
RUNTIME=$((END-BEGIN))
fancy_echo "Done in $RUNTIME seconds."