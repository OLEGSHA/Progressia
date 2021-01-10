# Build Guide

This document is a guide to building Progressia from source.

Compilation should be possible on all platforms that support JDK 8 or later, however, packaging scripts require Bash.

This guide assumes you are familiar with using a terminal or Windows Command Prompt or PowerShell.

See [Eclipse Guide](EclipseGuide.md) and [IntelliJ IDEA Guide](IntelliJIDEAGuide.md) for IDE-specific configuration.

## Basic compilation

### Installing prerequisites

Compiling Progressia requires that a JDK (Java Development Kit) 8 or later is available in your system path. Please
note that JDK is not the same as a JRE (Java Runtime Environment), the software required to execute Java programs;
you may be able to run Progressia but not compile it.

To check whether you have the correct JDK installed please run
	`javac --version`.
If this command fails or outputs version 1.7 or lower, then you need to (re-)install JDK. (javac is the Java
Compiler.)

Progressia developers recommend [OpenJDK](https://openjdk.java.net/) as the free, open-source Java implementation.
Confusion may arise from the project's name: OpenJDK issues both JRE and JDK; an OpenJDK JRE is not enough to compile
Progressia. Another popular choice is [Oracle Java](https://www.oracle.com/java/technologies/), which is proprietary
and thus not recommended.

To install OpenJDK JDK on GNU/Linux, install the appropriately named package with your package manager (on Debian,
Ubuntu and other dpkg-based distributions, run
	`apt-get install default-jdk`
with root privileges).

To install OpenJDK JDK on MacOS or Windows, Progressia developers recommend [AdoptOpenJDK](https://adoptopenjdk.net/),
which creates and distributes OpenJDK installers and packages. Please confirm that JDK installed correctly using
the aforementioned test.

### Getting the source code

Progressia source code is managed with [git](https://git-scm.com/), a popular open-source tool. It is a program that
automatically keeps track of changes in a set of source code and allows synchronization of those changes between
different computers. A copy of the source code is called a repository. This guide will assume that
[Progressia's GitHub repository](https://github.com/OLEGSHA/Progressia/) is accessible; if it is not, please look up
the relevant instructions.

If git is installed on your system, perform a clone of
	`https://github.com/OLEGSHA/Progressia.git`.
The details may vary depending on your git frontend; if you are not using a frontend, issue the following command:

```
git clone https://github.com/OLEGSHA/Progressia.git
```

Run the command in your directory of choice, git will create a subfolder. After the action completes you should
have a local copy of the source code.

If you do not have git installed, use GitHub's 'Download ZIP' option (available through the green 'Code' button at
the time of writing this). Unpack the downloaded archive into any directory.

### Compiling

Compilation and related tasks are managed by [Gradle](https://gradle.org/), another popular open-source tool. The repository
contains a Gradle Wrapper which automatically detects and installs the appropriate Gradle version. Gradle is most
conveniently controlled with one of two scripts, `gradlew` or `gradlew.bat` for Bash and Windows Command Prompt respectively,
which are distributed within the repository as well. Gradle itself uses another file, `build.gradle`, that contains the
relevant instructions in Groovy.

On GNU/Linux and MacOS, the script may need to receive the execution permission before being run:

```
chmod +x gradlew
```

A most basic compilation is achieved with:

```
./gradlew buildLocal
```

This instructs Gradle to compile, check and test a JAR archive with the game, as well as to download all necessary
dependencies. The resulting JAR file will only reference libraries required to run the game on your current platform.

The compiled JAR and all necessary libraries can be found in `build/libs/`. The `lib` directory next to `Progressia.jar`
contains all runtime dependencies; the game may be run with a simple
	`java -jar Progressia.jar`
if `lib` is located in the same directory as the JAR file. (On many systems double-clicking `Progressia.jar` has the
same effect.) Otherwise, if there is no `lib` directory next to the JAR, classpath must be set manually with command
line options for `java`.

A more conventional command, `./gradlew build`, may be used to the same effect; however, Progressia developers recommend
against using it as their effects might start to differ with future updates.

## Building for other platforms

### Native libraries and why platforms matter

Although Java is well-known for its portability, Progressia has a limited set of architectures that it supports.
This is a consequence of the fact that Progressia uses [OpenGL](https://en.wikipedia.org/wiki/OpenGL),
[OpenAL](https://en.wikipedia.org/wiki/OpenAL), [GLFW](https://www.glfw.org) and [STB](https://github.com/nothings/stb),
four libraries originally implemented in the C language. In order to access them, a Java wrapper library,
[LWJGL](https://www.lwjgl.org/), is used. Unfortunately such wrappers need to be hand-written for each platform
and require complicated compilation.

In practice this means that along with the pure Java libraries, such as Guava or GLM, Progressia carries several
LWJGL libraries that each consist of a Java interface and a compiled binary implementation (called a native library).
A separate version of a single native library is required to run Progressia on different platforms; if a game only
contains natives for Windows, it cannot be run on GNU/Linux.

### How Gradle is set up

Gradle builds Progressia by first choosing which platform or platforms should be supported. It then downloads,
if necessary, and processes only dependencies that are relevant to the current configuration. When creating the JAR
file, Gradle appends only selected libraries to the JAR's manifest file. It finally copies only the libraries included
in the manifest to the `build/libs/lib` directory.

Why not package all natives all the time? This is inefficient when producing temporary builds for development purposes
because unnecessary dependencies need to be downloaded and processed. This mechanism is also used to trim down the
size of various system-specific installers and packages since, for example, a Windows Installer is not expected to
produce a installation that runs on anything other than Windows, so GNU/Linux and MacOS natives need not be packaged
with it.

Unless instructed otherwise, Gradle only chooses the local platform. Thus the procedure described in 'Basic Compilation'
produces an output that only contains the natives for the platform that performed the compilation. It is unsuitable for
publication and may be generally inconvenient.

### Building cross-platform

Fortunately it is possible to produce a maximally cross-platform version by running Gradle with a different task:

```
./gradlew buildCrossPlatform
```

This command performs the same actions as the `buildLocal` command, except that all possible native libraries are
included in the JAR manifest and copied into `build/libs/lib`. The result may be run on any supported platform.
However, because of the large amount of libraries, it has significantly greater size than a platform-specific
build.

### Building for a specific platform

Some users might find the need to build for a specific set of platforms. Inclusion of GNU/Linux, Windows or MacOS
dependencies individually can be controlled with the following arguments to the `./gradlew build` command:
- `requestLinuxDependencies` requests that `natives-linux`, `natives-linux-arm32` and `natives-linux-arm64` binaries are included;
- `requestWindowsDependencies` requests that `natives-windows` and `natives-windows-x86` binaries are included;
- `requestMacOSDependencies` requests that `natives-macos` binaries are included.
These requests can be applied in any combination. For example, the following command produces a build containing only
GNU/Linux and Windows natives:

```
./gradlew build requestLinuxDependencies requestWindowsDependencies
```

For finer control please edit `build.gradle` manually by adding the desired natives to the `project.ext.platforms` set like so:

```
project.ext.platforms = new HashSet<>()
project.ext.platforms.add 'natives-windows-x86'
```

## Packaging

A Debian package and a Windows installer can be created automatically on systems that support Bash. These tasks are delegated
by Gradle to `buildPackages.sh` in repository root. This script checks the environment and assembles the requested output; the
resulting files are moved into `build_packages`.

### Creating a Debian package

A Debian package can be created with the following Gradle task:

```
./gradlew packageDebian
```

Gradle will then build all artifacts necessary to run the game on GNU/Linux (all three architectures) and invoke
`./buildPackages.sh debian`. Commands `dpkg-deb` and `fakeroot` must be available in system path in order to build the package.

### Creating a Windows installer

A Windows installer can be created with the following Gradle task:

```
./gradlew packageWindows
```

Gradle will then build all artifacts necessary to run the game on Windows (both x64 and x86 architectures) and invoke
`./buildPackages.sh windows`.

Windows installers are implemented with [NSIS](https://nsis.sourceforge.io/). Command `makensis` must be available in system
path in order to build the installer.

## Gradle tasks summary

- `buildLocal` – creates a build optimized for current platform. Use this to quickly build the game during development.
- `buildCrossPlatform` – creates a build that supports all known architectures. Use this to build a universal version of the game.
- `build` – currently a synonym of `buildLocal`; creates a default build.
- `packageDebian` – creates a Debian package. Do not invoke together with `packageWindows`.
- `packageWindows` – creates a Windows installer. Do not invoke together with `packageDebian`.
- `requestLinuxDependencies` – requests that `natives-linux`, `natives-linux-arm32` and `natives-linux-arm64` binaries are included when building.
- `requestWindowsDependencies` – requests that `natives-windows` and `natives-windows-x86` binaries are included when building.
- `requestMacOSDependencies` – requests that `natives-macos` binaries are included when building.
- `requestCrossPlatformDependencies` – requests that all binaries are included when building.

All other basic and Java-related Gradle tasks are available as well.