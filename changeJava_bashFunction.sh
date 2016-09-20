#
# Transplant the contents of this class into your
# .zshrc, .bashrc, or similar.
#

# Quick way to switch between java 7 and java 8
changeJava () {
  #
  # CHANGE THESE
  #
  # Location of groovy, probably within sdkman
  GROOVY_PATH=~/.sdkman/candidates/groovy/current/bin/groovy
  # Location of the ChangeJava.groovy script, possibly your ~/bin?
  SCRIPT_PATH=~/bin/ChangeJava.groovy
  # Make sure a JAVA_HOME (JDK, JRE maybe OK) exists so we can run Groovy
  JAVA_HOME=/usr/java/jdk1.8.0_101

  # Force JAVA_HOME/bin to the PATH so we know we can run groovy
  PATH=${JAVA_HOME}/bin:$PATH
  VERSION=$1
  JAVA_HOME_NEW=`${GROOVY_PATH} ${SCRIPT_PATH} --java-version ${VERSION} --java-home`
  if [ "x$JAVA_HOME_NEW" != "x" ]; then
      PATH_NEW=`${GROOVY_PATH} ${SCRIPT_PATH} --java-version ${VERSION} --path`
      export JAVA_HOME=$JAVA_HOME_NEW
      export PATH=$PATH_NEW
      echo "JAVA_HOME:" $JAVA_HOME
      java -version
   fi
}

# Default to Java 8
changeJava 1.8