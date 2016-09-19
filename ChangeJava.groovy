#!/usr/bin/env groovy

@Grab(group='com.beust', module='jcommander', version='1.30')

import com.beust.jcommander.Parameter
import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException

/**
 * See README.md for details on this script including installation and
 * usage.
 *
 * TODO: This script is kind of slow and it has to be executed twice to
 * TODO: be useful (about 2-3 seconds total). It chould probably be re-written in 
 * TODO: Kotlin, Ruby, Python, etc. for improved execution / startup time.
 */
class ChangeJava {

    /** Command line arguments for parsing. */
    String[] commandLineArgs

    /** Map of Java version name (like '1.7') to java home dir (full path). Populated by loadVersions() */
    Map<String, String> versions = [:]

    /** ChangeJava configuration file. */
    File configFile = "${System.env['HOME']}/.changejavarc" as File

    /** Default java version (like '1.7'). Populated by loadVersions(). */
    String defaultVersionKey

    /** If help should be displayed (how to use this script). */
    @Parameter(names = ['-h', '--help'], description = "Usage")
    boolean showHelp = false

    /** The java version to switch to. */
    @Parameter(names = ['-v', '--java-version'], description = "Java version to switch to, defaults to first entry in .changejavarc")
    String javaVersion = ""

    /** If the updated PATH that should be used is returned from this script. */
    @Parameter(names = ['-p', '--path'], description = "Show new PATH value.")
    boolean showPath = false

    /** If the updated JAVA_HOME that should be used is returned from this script (default). */
    @Parameter(names = ["-j", "--java-home"], description = "Show new JAVA_HOME value (default)")
    boolean showJavaHome = false

    /** Selected paths for selected javaHome (JDK and optionally JRE). */
    List<String> javaHomePaths

    /**
     * Kick things off.
     */
    public static void main(String[] args) {
        ChangeJava cj = new ChangeJava()
        cj.run(args)
    }

    /**
     * Well, really kick things off. Load the versions file, 
     * parse the command line, output the information requested.
     */
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

    /**
     * Figure out a new path with the specified version of java at the 
     * FRONT of that path. Additionally, remove any java versions from within
     * the path that might have been put in there earlier.
     */
    void showPath() {
        LinkedList<String> pathElements = System.env['PATH'].split(':') as LinkedList
        versions.each { version, javaPaths ->
            javaPaths.each { javaPath ->
                while (javaPath in pathElements) {
                    pathElements.remove javaPath
                }
                String javaBinPath = "${javaPath}/bin"
                while (javaBinPath in pathElements) {
                    pathElements.remove javaBinPath
                }
            }
        }
        javaHomePaths.reverse().each { javaHomePath ->
            pathElements.addFirst "${javaHomePath}/bin"
        }
        println pathElements.join(':')
    }

    /**
     * Output the desired JAVA_HOME for the specified version of Java.
     */
    void showJavaHome() {
        println javaHomePaths[0]
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
            String[] parts = line.split(/[:]/)
            if (parts.size() >= 2) {
                versions[parts[0]] = []
                parts.eachWithIndex { pathElement, index ->
                    if (index) {
                        // Don't copy the first one (version number). Just
                        // copy the additional path elements. Generally
                        // the first one is the JDK and the additional is
                        // a JRE, optionally.
                        versions[parts[0]] << pathElement
                    }
                }
            }
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
            javaHomePaths = versions[javaVersion]

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
