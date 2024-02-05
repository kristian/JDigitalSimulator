JDigitalSimulator: A platform independent Electronic Design Automation software
===============================================================================

[![Build Status](https://github.com/kristian/JDigitalSimulator/actions/workflows/maven.yml/badge.svg)](https://github.com/kristian/JDigitalSimulator/actions/workflows/maven.yml)

JDigitalSimulator is a platform independent open source Electronic Design Automation software entirely written in Java.

Please download the latest release from the [releases section](https://github.com/kristian/JDigitalSimulator/releases/latest) here on GitHub. Visit the [project homepage](http://kra.lc/projects/jdigitalsimulator/) for some additional information.

Run
---

To run JDigitalSimulator download the JAR file and run it with:
```
java -jar jds-2.x.x.jar
```
Note that if you are using a latest Java version (such as Java 21), it will require you to use a `--add-opens java.base/java.io=ALL-UNNAMED` to the command, in order to be able to open legacy files from JDigitalSimulator version > 1.*.

Build
-----

To build JDigitalSimulator on your machine, checkout the repository, `cd` into it, and call:
```
mvn clean install
```
In case you are trying to build on a 64bit Linux distribution, the 32bit Windows launcher will require the following libraries to be installed on your system `lib32z1`, `lib32ncurses5`, `lib32bz2-1.0` and `zlib1g:i386`.

License
-------

The code is available under the terms of the [GNU General Public License](https://opensource.org/licenses/GPL-3.0).