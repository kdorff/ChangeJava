# ChangeJava

## Introduction

I frequently manage Linux servers where I must maintain multiple Java versions.
I wanted to be able to easily switch (from the command line perspective) 
between various versions of Java. I played with "alternatives," but found it
to be a pain. So here we are.

## Contents

This package consists of a Groovy script (ChangeJava.groovy) and a
shell function you insert into your .zshrc, .bashrc, or whatever you use.
The two work in concert to help you redefine JAVA_HOME and PATH
for your current shell for the desired version of Java.

## Prerequisites

You need to install one or more versions of Java and a version of Groovy.
The easiest way to install Groovy is using [sdkman](http://sdkman.io/).


## Installation

Create the file ~/.changejavarc to list your installed Java versions
and the path to JAVA_HOME for each. A sample ~/.changejavarc might look like:

```
1.8:/usr/java/jdk1.8.0_101
1.7:/usr/java/jdk1.7.0_79
```

Pull the ChangeJava code from Github into a directory. Here we are pulling the
code to ```~/bin/ChangeJava```.

```
mkdir ~/bin
cd ~/bin
git clone git@github.com:kdorff/ChangeJava.git
```

Edit your .zshrc, .bashrc, etc. to add the changeJava function to your
environment by pre-configuring a few envionrment variables and
importing the changeJava function by sourcing the 
```changeJava_bashFunction.sh``` file.

Example content to add to your .zshrc, .bashrc:

```
#
# ChangeJava for quick java version changes for the current shell.
# https://github.com/kdorff/ChangeJava
#
# Path to the installed groovy binary
CJ_GROOVY_PATH=~/.sdkman/candidates/groovy/current/bin/groovy
# Path to the ChangeJava.groovy script
CJ_SCRIPT_PATH=~/bin/ChangeJava/ChangeJava.groovy
# Path to some java home directory
CJ_JAVA_HOME=/usr/java/jdk1.8.0_101
# Include the changeJava bash function for quick java version changes
. ~/bin/ChangeJava/changeJava_bashFunction.sh

# Set the default java for the shell
changeJava 1.8
```

## Usage

Looking at the contents of the ~/.changejavarc as demonstrated above,
if we want to use the Java that is installed to "/usr/java/jdk1.7.0_79", we
see that has a label of "1.7" so we can execute:

```
changeJava 1.7
```

## Updating ChangeJava from github

Assuming I don't break anything between releases, you can update ChangeJava
with the following commands

```
cd ~/bin/ChangeJava
git pull
```

## Common mistakes

* Make sure ChangeJava.groovy has executable permissions.
* Verfiy you are defining the variables ```CJ_GROOVY_PATH```, 
  ```CJ_SCRIPT_PATH```, ```CJ_JAVA_HOME``` before sourcing the script
  file ```changeJava_bashFunction.sh```
