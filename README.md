# ChangeJava

## Introduction

I frequently manager Linux servers where I must maintain multiple Java versions.
I wanted to be able to easily switch (from the command line perspective) 
between various versions of Java. I played with "alternatives" but found it
to be a pain. So here we are.

## Contents

This package consists of a Groovy script (ChangeJava.groovy) and a
function you insert into your .zshrc, .bashrc or whatever you use.
The two work in concert to help you redefine JAVA_HOME and PATH
for your current shell.

## Prerequisites

You need to install one or more versions of Java and Groovy.
The easiest way to install Groovy is using sdkman.

## Installation

Create the file ~/.changejavarc to enumerate your installed versions
of java and the path to their home.

```
1.8:/usr/java/jdk1.8.0_101
1.7:/usr/java/jdk1.7.0_79
```

Insert the contents of the file ```shell_function.sh``` into your
.bashrc, .zshrc, or similar and modify as necessary. Part of the
modification is to specify the locations of GROOVY_PATH and SCRIPT_PATH.

## Usage

Looking at the contents of the ~/.changejavarc as demonstrated above,
if we want to use the java that is installed to "/usr/java/jdk1.7.0_79", we
see that has a label of "1.7" so we can execute (from the command line)

```
changeJava 1.7
```

## Common mistakes

Make sure ChangeJava.groovy has executable permissions.
