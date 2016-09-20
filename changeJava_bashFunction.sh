#
# Bash, zsh function to change the version of Java provided to the current shell.
# Source or . this file from your .zshrc, .bashrc, or similar.
#

# Quick way to switch between java 7 and java 8
changeJava () {
  DESIRED_VERSION=$1

  if [ "x${CJ_GROOVY_PATH}" = "x" ]; then
    echo "Missing variable CJ_GROOVY_PATH"
    echo "This variable defines the path to the installed groovy binary"
    return
  fi
  if [ "x${CJ_SCRIPT_PATH}" = "x" ]; then
    echo "Missing variable CJ_SCRIPT_PATH"
    echo "This variable defines the path to the ChangeJava.groovy script"
    return
  fi
  if [ "x${CJ_JAVA_HOME}" = "x" ]; then
    echo "Missing variable CJ_JAVA_HOME"
    echo "This variable defines the path to some java home directory"
    return
  fi

  # Make sure a JAVA_HOME (JDK, JRE maybe OK) exists so we can run Groovy
  export JAVA_HOME=${CJ_JAVA_HOME}

  # Force JAVA_HOME/bin to the PATH so we know we can run groovy
  export PATH=${JAVA_HOME}/bin:$PATH
  JAVA_HOME_NEW=`${CJ_GROOVY_PATH} ${CJ_SCRIPT_PATH} --java-version ${DESIRED_VERSION} --java-home`
  if [ "x$JAVA_HOME_NEW" != "x" ]; then
      PATH_NEW=`${CJ_GROOVY_PATH} ${CJ_SCRIPT_PATH} --java-version ${DESIRED_VERSION} --path`
      export JAVA_HOME=$JAVA_HOME_NEW
      export PATH=$PATH_NEW
      echo "JAVA_HOME: ${JAVA_HOME}"
      java -version
   fi
}
