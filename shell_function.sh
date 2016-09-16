#
# Transplant the contents of this class into your
# .zshrc, .bashrc, or similar.
#

# Quick way to switch between java 7 and java 8
changeJava () {
  GROOVY_PATH=~kevi9037/.sdkman/candidates/groovy/current/bin/groovy
  SCRIPT_PATH=~kevi9037/bin/ChangeJava.groovy
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