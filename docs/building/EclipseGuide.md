### Eclipse Guide

This document is a guide to configuring Eclipse IDE for developing Progressia.

## Setup

### Downloading project

Although the project may be downloaded manually or using git directly (as described in the
[Build Guide](BuildGuide.md)), it may be more convenient to use Eclipse's EGit. If you
choose not to, skip the following subsection.

#### Using EGit

[EGit](https://www.eclipse.org/egit/) is a git interface for Eclipse. It is currently shipped
with Eclipse IDE.

1. Open Git perspective. (Git perspective is not visible by default. To open it, use
'Window' menu > 'Perspective' > 'Open Perspective' > 'Other' > select Git.)
2. In 'Git Repositories' view, click 'Clone a Git Repository and add the clone to this view'
button.
3. Paste
`https://github.com/OLEGSHA/Progressia.git`
or the appropriate git URI into the 'URI' field and click 'Next'.
4. Review the branches and click 'Next'.
5. Edit the local repository path if necessary and click 'Finish'.

Note: avoid importing the project from the Git Clone Wizard as doing so will fail to specify
Gradle dependencies.

### Importing project

Gradle dependencies need to be imported into the IDE for proper code analysis and launching.

#### Using Buildship plugin for Eclipse

[Buildship](https://projects.eclipse.org/projects/tools.buildship) is an Eclipse plugin
that integrates Gradle into the IDE. This is the recommended method.

1. In 'File' menu, select 'Import...'.
2. In the Import Wizard, select 'Gradle' > 'Existing Gradle Project'. Click 'Next'.
3. In the Gradle Import Wizard, click 'Next' to arrive at directory selection.
4. Select the directory that contains `build.gradle` file in 'Project root directory' field.
5. Click 'Finish' and allow the plugin to import the project.

When using this method, any changes to the `build.gradle` file must be followed by a Gradle
refresh ((Project context menu) > 'Gradle' > 'Refresh Gradle Project').


#### Using Eclipse plugin for Gradle

Gradle features a plugin for Eclipse that can generate project specifications for the IDE.
It is deactivated by default.

1. Enable the Eclipse plugin by uncommenting the `id 'eclipse'` line in `build.gradle`
(note the disappearance of `//`). Please make sure not to accidentally commit this change in git!

```
plugins {
    // Apply the java-library plugin to add support for Java Library
    id 'java-library'
    
    /*
     * Uncomment the following line to enable the Eclipse plugin.
     * This is only necessary if you don't use Buildship plugin from the IDE
     */
    id 'eclipse'
}
```

2. Run
	`./gradlew eclipse`
in the directory that contains `build.gradle`. This command will
generate Eclipse project files.
3. In 'File' menu in the Eclipse IDE, select 'Import...'.
4. In the Import Wizard, select 'General' > 'Existing Projects into Workspace'. Click 'Next'.
5. Select the directory that contains `build.gradle` file in 'Select root directory' field.
6. Click 'Finish' and allow the IDE to import the project.

When using this method, any changes to the `build.gradle` file must be followed by
`./gradlew eclipse` command and a project refresh ((Project context menu) > 'Refresh').


### Creating a Run Configuration

Run configurations are used by Eclipse IDE to specify how a project must be run.

1. In 'Run' menu, select 'Run Configurations...'.
2. In the Run Configurations, select 'Java Application', then click 'New launch configuration'.
3. Specify the project and the name of the new configuration.
4. Put
`ru.windcorp.progressia.client.ProgressiaClientMain`
into 'Main Class' field.
5. In the 'Arguments' tab, put
`${workspace_loc:Progressia}/run`
into 'Working directory:' > 'Other' field. Replace `Progressia` with your name of the project.
Alternatively specify another location outside of the project's root directory.
6. Click 'Apply' to save changes. Exit Run Configurations.

Step 5 is required to specify that the game must run in some directory other than the project root,
which is the default in Eclipse.

## Common problems

### Buildship plugin fails with a cryptic message

This may be caused by a lack of Java in your system path. Eclipse stores the path to the JVM it
uses in its settings and is thus not affected by the changes to the system path. However, Gradle
searches for Java installations in system path regardless and may fail independently of Eclipse.

__Solution:__ the simplest solution is to reinstall JDK making sure that system path is affected.
See [Build Guide](BuildGuide.md) for details. Another course of action is to manually append the
Java installation directory (specifically its `bin` folder) to the system `PATH` variable.