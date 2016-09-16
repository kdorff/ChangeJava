#!/usr/bin/env groovy

@Grab(group='com.beust', module='jcommander', version='1.30')

import com.beust.jcommander.Parameter
import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException

/**
 * Insert the following into your .zshrc or whatever startup script 
 * you use for your shell. Change paths as appropriate.
 * -------------------

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

 *
 * and associated ~/.changejavarc
 * -------------------
 * 1.8:/Library/Java/JavaVirtualMachines/jdk1.8.0_92.jdk/Contents/Home
 * 1.7:/Library/Java/JavaVirtualMachines/jdk1.7.0_80.jdk/Contents/Home
 *
 * This script is kind of slow and it has to be executed twice to
 * be useful (about 2-3 seconds total). It should probably be re-written in 
 * Kotlin, Ruby, Python, etc. for improved execution / startup time.
 */
class ChangeJava {

    /** Command line arguments for parsing. */
    String[] commandLineArgs

    /** Map of Java version name (like '1.7') to java home dir (full path). Populated by loadVersions() */
    Map<String, String> versions = [:]

    /** Configuration file. */
    File configFile = "${System.env['HOME']}/.changejavarc" as File

    /** Default java version (like '1.7'). Populated by loadVersions() */
    String defaultVersionKey

    @Parameter(names = ['-h', '--help'], description = "Usage")
    boolean showHelp = false

    @Parameter(names = ['-v', '--java-version'], description = "Java version to switch to, defaults to first entry in .changejavarc")
    String javaVersion = ""

    @Parameter(names = ['-p', '--path'], description = "Show new PATH value.")
    boolean showPath = false

    @Parameter(names = ["-j", "--java-home"], description = "Show new JAVA_HOME value (default)")
    boolean showJavaHome = false

    /** Selected javaHome. */
    String javaHome

    public static void main(String[] args) {
        ChangeJava cj = new ChangeJava()
        cj.run(args)
    }

    void run(String[] args) {
        commandLineArgs = args
        if (!loadVersions()) {
            return
        }
        if (!parseCommandLine()) {
            return
        }

        if (showPath) {
            showPath()
        }
        else if (showJavaHome) {
            showJavaHome()
        }
    }

    void showPath() {
        LinkedList<String> pathElements = System.env['PATH'].split(':') as LinkedList
        versions.each { version, oneHomePath ->
            while (oneHomePath in pathElements) {
                pathElements.remove oneHomePath
            }
            String oneHomeBinPath = "${oneHomePath}/bin"
            while (oneHomeBinPath in pathElements) {
                pathElements.remove oneHomeBinPath
            }
        }
        pathElements.addFirst "${javaHome}/bin"
        println pathElements.join(':')
    }

    void showJavaHome() {
        println javaHome
    }

    /**
     * Load then config file into versions and defaultVersionKey.
     */
    boolean loadVersions() {
        if (!configFile.exists()) {
            System.err.println("Could not locate version file ${configFile}")
            return false
        }
        versions.clear()
        List<String> config = configFile.readLines()
        config.collect { line ->
            line.trim()
        }.findAll { line ->
            boolean found = line && !line.startsWith('#') && line.contains(':')
            if (!found && line) {
                System.err.println "Ignoring invalid .changejavarc line ${line}"
            }
            return found
        }.each { line ->
            String[] parts = line.split(/[:]/, 2)
            versions[parts[0]] = parts[1]
            if (defaultVersionKey == null) {
                defaultVersionKey = parts[0]
            }
        }

        if (!versions) {
            System.err.println "No java versions found in file ${configFile}"
            return false
        }

        // We have loaded at least one version.
        return true
    }

    /**
     * Parse command line args.
     * @param args command line arguments
     */
    boolean parseCommandLine() {
        JCommander jcommander = new JCommander(this)
        jcommander.setProgramName("ChangeJava.groovy");
        try {
            jcommander.parse(commandLineArgs)
            if (showHelp) {
                jcommander.usage()
                return false
            }

            // default
            if (!showPath && !showJavaHome) {
                showJavaHome = true
            }
            // More than one option selected
            if (showPath && showJavaHome) {
                showPath = false
            }

            // default
            Set<String> versionKeys = versions.keySet()
            if (javaVersion) {
                if (!(javaVersion in versionKeys)) {
                    System.err.print "Specified java version ${javaVersion} not found in ${configFile}"
                    return false
                }
            }
            else {
                javaVersion = versionKeys[0]
            }
            javaHome = versions[javaVersion]

            return true
        } catch (ParameterException e) {
            System.err.println ""
            System.err.println e.message
            System.err.println ""
            jcommander.usage()
            return false
        }
    }
}
