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

Legacy
------

In the pre-open-source era JDigitalSimulator was released without a license in version 1.* under an old `de.ksquared` namespace. With version 2.* the code was released on GitHub under the GNU General Public License. The released code was cleaned up and moved to a new namespace `lc.kra`.

### Opening Old Worksheets / Simulations

Since version 2.0.2 and later on fixed with version 2.3.0, code was added so that old JDigitalSimulator worksheets of version 1.* can be opened in the latest version of the tool. If you are using a later version of Java, e.g. Java 21 the following command line parameter will be necessary in order to be able to load old files:

```
--add-opens java.base/java.io=ALL-UNNAMED
```

After you re-saved the worksheet with a new version of this tool, you can again omit this command line parameter.

### Old / Legacy Plugin Support

JDigitalSimulator supports a plugin concept, as described on the [homepage here](https://kra.lc/projects/jdigitalsimulator/catalog.html). If you are interested to write a plugin on your own, check out the [example plugin files](/src/main/java/lc/kra/jds/components/plugin/).

Since the namespace was changed from `de.ksquared` to `lc.kra`, old plugins that are still using imports from the `de.ksquared` package, will no longer work. Since version 2.5.0 JDigitalSimulator supports loading of legacy plugins with the help of transpiling their code using [`ASM`](https://asm.ow2.io/). In order to enable this legacy mode, put your 1.* plugin JARs into the `/plugins` folder as you would do normally. Also download [`asm.jar`](https://repository.ow2.org/nexus/service/local/repositories/releases/content/org/ow2/asm/asm/9.6/asm-9.6.jar) and [`asm-commons.jar`](https://repository.ow2.org/nexus/service/local/repositories/releases/content/org/ow2/asm/asm-commons/9.6/asm-commons-9.6.jar) and put them into the `/plugins` folder alongside your plugin files. In case you are using a later version of the Java JDK, e.g. Java 21, you will also need to add the following command line parameter when starting your JVM:

```
--add-opens java.base/jdk.internal.loader=ALL-UNNAMED
```

When now launching JDigitalSimulator through the JAR file or the .exe provided, JDigitalSimulator will use the ASM JAR files, to transpile the old plugins on the fly.

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