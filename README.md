# Progressia
A free, open source sandbox survival game currently in early development.

## Contributing

For now, contact @Javapony in Telegram for details. Contributing is completely allowed, but we don't have any set guidelines yet.

## Building

1. `$ git clone https://github.com/OLEGSHA/Progressia.git`
2. `$ gradlew build`

### Additional setup for Eclipse IDE

If you have Buildship plugin installed, use File - Import - Gradle - Existing Gradle Project. Main class is `ru.windcorp.progressia.client.ProgressiaClientMain`.

Alternatively do the following:

1. Add `id 'eclipse'` into `build.gradle` inside `plugins { ... }`:
```
plugins {
    // Apply the java-library plugin to add support for Java Library
    id 'java-library'
    id 'eclipse'
}
```
2. `$ gradlew eclipse`
3. Import the project with File - Import - Existing Projects into Workspace

### Additional setup for IntelliJ IDEA

1. Open the project with File - Open Project
2. Press button 'Add configuration...' and open 'Application' in templates list
3. Add 'ru.windcorp.progressia.client.ProgressiaClientMain' in Main class text field
4. Choose 'Progressia.main' in 'Use classpath of module' drop-down list
5. Click 'Create configuration' and press Apply

## Libraries

* LWJGL - OpenGL, OpenAL, GLFW and several more libraries ported to Java
* Google Guava
* Trove4j
* java-graphics/glm - GLM ported to Java. _Maven Central contains an outdated version, a custom repository used instead_
* Apache Commons Math (_not currently used_)
