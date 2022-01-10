# Build Script Reference

This document is a user's reference for the build script of Progressia. For a beginner-friendly guide, see
[Build Guide](BuildGuide.md).

## Gradle tasks summary

- `buildLocal` – creates a build optimized for current platform. Use this to quickly build the game during development.
- `buildCrossPlatform` – creates a build that supports all known architectures. Use this to build a universal version of the game.
- `build` – currently a synonym of `buildLocal`; creates a default build.
- `packageZip` – creates a universal ZIP. Incompatible with other `package` tasks.
- `packageDeb` – creates a Debian package. Incompatible with other `package` tasks.
- `packageNsis` – creates a Windows NSIS installer. Incompatible with other `package` tasks.
- `requestLinuxDependencies` – requests that `natives-linux`, `natives-linux-arm32` and `natives-linux-arm64` binaries are included when building.
- `requestWindowsDependencies` – requests that `natives-windows`, `natives-windows-arm64` and `natives-windows-x86` binaries are included when building.
- `requestMacOSDependencies` – requests that `natives-macos` and `natives-macos-arm64` binaries are included when building.
- `requestCrossPlatformDependencies` – requests that all binaries are included when building.

To execute a task, run `./gradlew <task-name>`.

`build`-type tasks output the executable JAR and all libraries required at runtime into `build/libs`. `package`-type
tasks output packages into `build/packages`.

## Packaging tasks

Some packaging tasks require additional software in `PATH`.

| Task          | Commands required in `PATH`              |
|---------------|------------------------------------------|
| `packageDeb`  | `dpkg-deb`                               |
| `packageZip`  | _none_                                   |
| `packageNsis` | `makensis`, `convert` (from ImageMagick) |

## Version and metadata

### Version scheme

Progressia builds are identified by four parameters: version, Git commit, Git branch and build ID.

Versions roughly follow [semantic versioning](https://semver.org/spec/v2.0.0.html), with each version fitting the
`MAJOR.MINOR.PATCH[-SUFFIX]` pattern. Depending on the build environment (see below), version is either "real" with
no metadata (e.g. `0.43.2` or `1.2.1-beta`) or a dummy fallback with build metadata (e.g. `999.0.0-2021_07_23` or
`999.0.0-WJ3`).

### Version detection

Build script considers three scenarios when determining the version:

1. `version` project property is set explicitly. This may be done in a variety of ways, for example with command line
argument `-Pversion=1.2.3`
(see [Gradle docs](https://docs.gradle.org/current/userguide/build_environment.html#sec:project_properties))
2. Local Git repository is found, and HEAD is tagged appropriately: version is the tag name with leading `v`
stripped. Example: `v1.2.3` is version `1.2.3`
3. Local Git repository is found, and some ancestor of HEAD is tagged appropriately: version is the tag name with
leading `v` stripped and PATCH incremented by one. Example: `v1.2.3` is version `1.2.4`

Tags not named like `vMAJOR.MINOR.PATCH[-SUFFIX]` are ignored for cases 2 and 3.

In all other cases, a fallback dummy value is used for version, appended with build ID or current date.

### Git metadata

Git commit and Git branch are correspond to the state of the local Git repository, if any. In case Git metadata is
unavailable, `-` fallback is used for both fields.

### Build ID

Build ID uniquely identifies artifacts produced by automated build systems. For example, builds executed by WindCorp
Jenkins suite have build IDs like `WJ3` or `WJ142`. Build ID must be provided explicitly; it is `-` unless specified
otherwise.

Build ID may be set with `buildId` project property. This may be done in a variety of ways, for example with command
line argument `-PbuildId=WJ3`
(see [Gradle docs](https://docs.gradle.org/current/userguide/build_environment.html#sec:project_properties)).

## Native libraries

LWJGL uses native libraries. Build script declares platform-specific dependencies based on the set of target
platforms, `project.ext.lwjgl.targets` (aka `lwjgl.targets`). These dependencies are added to `runtimeOnly`
configuration.

When this set is empty, the script selects natives for current platform. Otherwise, all platforms in the set are
included.

`lwjgl.targets` is populated automatically by packaging tasks and by `buildCrossPlatform`. To add extra targets,
``requestXxxDependencies` tasks may be used.

Target selection mechanism may be overridden with `forceTargets` project property. This may be done in a variety of
ways, for example with command line argument `-PforceTargets=windows-x86,local`
(see [Gradle docs](https://docs.gradle.org/current/userguide/build_environment.html#sec:project_properties)). The
value is a comma-separated list of target architectures. `local` target will be replaced with the automatically
detected current architecture.

### Available targets

| Name            | Task                         |
|-----------------|------------------------------|
| `linux`         | `requestLinuxDependencies`   |
| `linux-arm32`   | `requestLinuxDependencies`   |
| `linux-arm64`   | `requestLinuxDependencies`   |
| `windows`       | `requestWindowsDependencies` |
| `windows-arm64` | `requestWindowsDependencies` |
| `windows-x86`   | `requestWindowsDependencies` |
| `macos`         | `requestMacOSDependencies`   |
| `macos-arm64`   | `requestMacOSDependencies`   |
