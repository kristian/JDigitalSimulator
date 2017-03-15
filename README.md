JDigitalSimulator: A platform independent Electronic Design Automation software
===============================================================================

[![Build Status](https://travis-ci.org/kristian/JDigitalSimulator.svg?branch=master)](https://travis-ci.org/kristian/JDigitalSimulator)

JDigitalSimulator is a platform independent open source Electronic Design Automation software entirely written in Java.

Please download the latest release from the [releases section](https://github.com/kristian/JDigitalSimulator/releases/latest) here on GitHub. Visit the [project homepage](http://kra.lc/projects/jdigitalsimulator/) for some additional information.

Build
-----

To build `JDigitalSimulator` on your machine, checkout the repository, `cd` into it, and call:
```
mvn clean install
```
In case you are trying to build on a 64bit Linux distribution, the 32bit Windows launcher will require the following libraries to be installed on your system `lib32z1`, `lib32ncurses5`, `lib32bz2-1.0` and `zlib1g:i386`.

License
-------

The code is available under the terms of the [GNU General Public License](https://opensource.org/licenses/GPL-3.0).