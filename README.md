# Progressia

:warning: **Important:** this Java version of Progressia is no longer developed. **We migrated to a different repository**, Wind-Corporation/Progressia ([GitHub](https://github.com/Wind-Corporation/Progressia), [windcorp.ru](https://gitea.windcorp.ru/Wind-Corporation/Progressia)), where development continues in C++.

A free, open-source sandbox survival game currently in early development.

## Description

The game has barely begun development so much of its features are yet to be implemented.

In broader terms, Progressia is a challenging game about survival, exploration and
engineering in a realistic voxel sandbox environment. The game is heavily inspired by
Minecraft technology mods, Factorio, Vintage Story and Minetest. Progressia's main unique
features will include highly composite items and blocks, a realistically-scaled world,
temperature mechanics and a parallelism-capable server.

## System requirements

- GNU/Linux (x64, arm32 or arm64), Windows XP or later (x64 or x86) or MacOS (x64)
- Java 8 or later
- OpenGL 2.1 or later
- Probably about 0.5 GiB RAM
- Less than 1 GiB of storage space

See [Build Guide](docs/building/BuildGuide.md) for compilation requirements.

## Contributing

All contributors welcome. Please contact Javapony in [Telegram](https://t.me/javapony)
or join our [Discord server](https://discord.gg/M4ukyPYgGP) for details or help.

## Building

On GNU/Linux and MacOS:

1. `$ git clone https://github.com/OLEGSHA/Progressia.git`
2. `$ chmod +x gradlew`
3. `$ ./gradlew buildLocal`

On Windows:

1. `git clone https://github.com/OLEGSHA/Progressia.git`
2. `gradlew.bat buildLocal`

Alternatively use Linux/MacOS steps in a Bash shell.

For a more in-depth explanation, solutions for common problems and tips for IDE configuration
please see the [Build Guide](docs/building/BuildGuide.md).

## Libraries

- [LWJGL](https://www.lwjgl.org/) ([GitHub](https://github.com/LWJGL/lwjgl3)) – OpenGL, OpenAL, GLFW and STB libraries ported to Java
  - [OpenGL](https://en.wikipedia.org/wiki/OpenGL) – a low-level graphics interface
  - [OpenAL](https://en.wikipedia.org/wiki/OpenAL) – a low-level audio interface
  - [GLFW](https://www.glfw.org/) ([GitHub](https://github.com/glfw/glfw)) – a minimalistic OpenGL-capable windowing library
  - [STB (GitHub)](https://github.com/nothings/stb) – a collection of various algorithms. `stb_vorbis` is used
- [Guava (GitHub)](https://github.com/google/guava) – a generic utilities library
- [Trove4j (BitBucket)](https://bitbucket.org/trove4j/trove) – optimized primitive collections 
- [java-graphics/glm (GitHub)](https://github.com/java-graphics/glm) – GLM ported to Java. _Maven Central contains an outdated version, a custom repository used instead_
- [OpenSimplex2 (GitHub)](https://github.com/KdotJPG/OpenSimplex2) – a minimalistic highly optimized noise generator
- [Log4j](https://logging.apache.org/log4j/2.x/) [(GitHub)](https://github.com/apache/logging-log4j2) – a logging library
