# Contributing Guidelines

This document lists conventions adopted by Progressia developers.

## git

### Branches
Progressia repository contains a `master` branch and several "feature" branches.

`master` is expected to contain a version of the game suitable for demonstration and forking/branching. Do not commit directly to `master` without OLEGSHA's approval.
- `master` must always correctly build without compiler warnings (see below).
- `master` must always pass all unit tests.
- `master` must always be able to launch successfully.
- `master` must always only contain working features.
- `master` should not contain excessive debug code.
- `master` must always have its code and filenames formatted (see below).

"Feature" branches are branches dedicated to the development of a single feature. When the feature reaches completion the branch is merged into `master` and removed. Intermediate merges into `master` may occur when some fitting milestone is reached. Intermediate merges from `master` may be done as necessary. Merges between "feature" branches should generally be avoided.

When beginning work on a new feature, create a new branch based on `master` (or on another "feature" branch if absolutely necessary). Use `all-small-with-dashes` to name the branch: `add-trees` or `rebalance-plastics` are good names. Do not fix unrelated bugs or work on unrelated features in a "feature" branch - create a new one, switch to an existing one or commit directly to `master` if the changes are small enough.

"Feature" branches may not be formatted properly. Formatting is required before merging into `master` or other branches.

### Commits
- Commits must leave the branch in a state that builds without compiler warnings (see below).
- Changes should be grouped in commits semantically. Avoid committing many small related changes in sequence; if necessary, wait and accumulate them. Avoid committing unrelated changes together; if necessary, split staged changes into several commits. This should normally result in about 1-2 commits for a day's work.
- Commit bulk changes (renaming, formatting, ...) separately. Don't ever commit whitespace changes outside formatting commits.
- Message format:

```
Short description of changes
<empty line>
- Enumeration of changes
  - Nest when appropriate
- Use dashes only
- List not needed for small commits
```

Example:

```
Changed packages for relations, renamed Face to ShapePart

- Added BlockRelation as an abstract superclass to existing relations
  - Must be given an absolute "up" direction before use
- Moved AbsFace, AbsRelation and BlockRelation to .world.rels
- Renamed Face to ShapePart to reduce confusion with AbsFace
```

- Only commit changes described in the commit message. Please double-check staged files before committing.
- Avoid merge conflicts. Pull before committing.
- Better sign commits than not. See: [git](https://git-scm.com/book/en/v2/Git-Tools-Signing-Your-Work), [GitHub](https://docs.github.com/en/github/authenticating-to-github/managing-commit-signature-verification).

## Code

### Warnings
Make sure that all committed code contains no compiler warnings. This specifically includes unused imports, unused private members, missing `@Override`s and warnings related to generics.

Warnings about unknown tokens in `@SuppressWarnings` are temporarily ignored. Please disable them in your IDE.

### Code Style
Formatting code is important.

- The format is specified within the files inside `/templates_and_presets/eclipse_ide`. Import the specifications into Eclipse or IntelliJ IDEA and use the IDEs' format feature. Alternatively format the code manually in accordance with existing files.
- Only use tabs for indentation. Never indent with spaces even when wrapping lines. This is to ensure that indentation does not break when tab width is different.
- Don't use `I` prefix for interfaces (not `IDoable` but `Doable`).
- Prioritize readability over compactness. Do not hesitate to use (very) long identifiers if they aid comprehension.
- Document all mathematics unless it is trivial, especially when using math notation for variable names.
- Use proper English when writing comments. Avoid boxes in comments. Use `//` for single-line comments.