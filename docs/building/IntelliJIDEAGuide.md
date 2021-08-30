# Intellij IDEA Guide

This document is a guide to configuring Intellij IDEA for developing Progressia.

## Setup

### Downloading project

Although the project may be downloaded manually or using git directly (as described in the
[Build Guide](BuildGuide.md)), it may be more convenient to use pre-installed Intellij IDEA Git extension. If you choose not to, skip the following subsection.

#### Using Git Extension

[Git extension](https://plugins.jetbrains.com/plugin/13173-git) is a git interface for Intellij IDEA. It is currently shipped with Intellij IDEA.

1. Open Intellij IDEA welcome screen, click the 'Get from VCS' button. Alternatively, use 'File' menu > 'New' > 'Project From Version Control...'.
2. Insert `https://github.com/OLEGSHA/Progressia.git` in the 'URL' field.
3. Change the local repository path, if necessary, and click 'Clone'.

### Creating a Run Configuration

Run configurations are used by Intellij IDEA to specify how a project must be run.

#### Using GUI shortcut

1. In Project Explorer, open `ru.windcorp.progressia.client.ProgressiaClientMain`.
2. Press small green triangle button in code editor window.
3. In the drop-down menu, click 'Modify Run Configuration...'.
4. Append `\run` to the 'Working directory' field. Alternatively, specify another location outside of the project's root directory.
5. Click 'Apply' to save changes.

#### Using 'Add Configuration' menu

1. Click 'Add Configuration...'.
2. In 'Run/Debug Configurations', click 'Add new configuration' with the plus icon.
3. In the drop-down list, select 'Application'.
4. Enter the configuration name .
5. Select a JRE from the drop-down list.
6. Select the module whose classpath should be used to run the application: `Progressia.main`.
7. Enter `ru.windcorp.progressia.client.ProgressiaClientMain` in the 'Main Class' field.
8. Append `\run` to the 'Working directory' field. Alternatively, specify another location outside of the project's root directory.
9. Click 'Apply' to save changes.

Step 8 is required to specify that the game must run in some directory other than the project root, which is the default in Intellij IDEA.

### Applying formatting templates

Windcorp's Progressia repository is formatted with a style defined for Eclipse IDE (sic) in
`templates_and_presets/eclipse_ide`.
Please apply these templates to the project to automatically format the source in a similar fashion.

1. In project context menu, click 'File->Properties'. (`Ctrl+Alt+S`)
2. In 'Editor' > 'Code Style' > 'Java', press gear icon, then click 'Import Scheme' > 'Eclipse code style'
3. In Scheme select 'Project'
4. Open the file `templates_and_presets/eclipse_ide/FormatterProfile.xml` in 'Select Path'.
5. Inside 'Import Scheme' widow click 'Current Scheme' check box after press OK

