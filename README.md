
sbt-package-dist
================

sbt-package-dist is a plugin for sbt 0.11 which attempts to codify best
practices for building, packaging, and publishing libraries and servers. It
adds the following features:

- a "package-dist" task for creating a deployable distribution of a server,
  including an executable jar and a `build.properties` file
- support for a default set of repos where most of the common libraries are
- the ability to send all repo requests through a proxy, for highly
  firewalled environments
- the ability to publish into an svn repo (using ivy-svn)
- commands to make it easier to follow semantic versioning for releases,
  including tagging releases in git

General Usage
=============

## Getting sbt-package-dist

See https://github.com/harrah/xsbt/wiki/Plugins for information on adding
plugins. In general, you'll need to add the following to your
project/plugins.sbt file:

    addSbtPlugin("com.twitter" % "sbt-package-dist" % "1.0.0")

## Mixing in sbt-package-dist

sbt-package-dist is a collection of many different mixins. Each mixin provides
a set of SBT Settings, Tasks and/or Commands that your project can use. The
way an SBT project "uses" these extensions is by adding them to the project's
settings map. There are two ways to include settings into your build
definition.

### Using an .sbt file

If you want to include all of the settings:

    import com.twitter.sbt._

    seq(StandardProject.newSettings: _*)

If you want to include only a specific mixin's settings, you can specify just
the one(s) you want:

    import com.twitter.sbt._

    seq(GitProject.gitSettings: _*)

### Using a .scala build definition

In your scala build definition, just extend the settings of any defined
projects:

    import sbt._
    import Keys._
    import com.twitter.sbt._

    object MyProject extends Build {
      lazy val root = Project(
        id = "my-project",
        base = file("."),
        settings = StandardProject.newSettings
      )
    }

Reference
=========

## Plugins

Standard project provides the following plugins you can extend:

### StandardProject

Aggregates a "reasonable" set of mixins into a single plugin. Currently
included are:

* DefaultRepos
* ArtifactoryPublisher
* SubversionPublisher
* GitProject
* BuildProperties
* PublishSourcesAndJavadocs
* PackageDist
* VersionManagement
* ReleaseManagement

### DefaultRepos

Sets up a default set of maven repos used in most Twitter projects. If the
`SBT_PROXY_REPO` environment variable is set, it uses a proxy repo instead.
Using a proxy repo may be useful when building in an environment where sbt
can't access the internet.

### ArtifactoryPublisher

Publishes artifacts to an "artifactory" (optionally with credentials) instead
of the default publishing target. This mixin only takes effect if
`artifactoryResolver` is set.

### SubversionPublisher

Publishes artifacts to a subversion repo, if one is set. Settings of interest
are:

* subversion-prefs-file - your credentials go in this file
* subversion-username - if you want to hardcode this
* subversion-password - if you're naughty and want to put this in source
  control
* subversion-repository - the url of your svn repo

### GitProject

Adds various settings and commands for dealing with git-based projects,
including:

* git-is-repository
* git-project-sha
* git-last-commits-count
* git-last-commits
* git-branch-name
* git-commit-message
* git-commit
* git-tag
* git-tag-name

### BuildProperties

Uses `GitProject` to write a `build.properties` file containing information
about the environment used to produce a jar. Includes the name, version, build
name (usually a timestamp), git revision, git branch, and the last few
commits.

### PublishSourcesAndJavadocs

Just works around a bug in sbt to prevent scaladoc from blowing up.

### PackageDist

Generates a deployable zip file of a server, containing:

- the executable jar (with manifest classpath and `build.properties`)
- any dependencies, in `lib/`
- any scripts, in `scripts/`
- any config files, in `config/`

### VersionManagement

Provides commands to modify the current project version, including:

* version-bump-major - tick major rev by 1, reset minor/patch to zero
* version-bump-minor - tick minor rev by 1, reset patch to zero
* version-bump-patch - tick patch by 1
* version-to-snapshot - add -SNAPSHOT to the current version
* version-to-stable - remove -SNAPSHOT from the current version
* version-set - set to an arbitrary version

These commands look in `build.sbt` and `project/*.scala` files for a current
version string and replace it with the updated version. After this is done,
the command reloads the project, which should now have the updated version.

### ReleaseManagement

Provides commands for publishing a release. The primary command is `release-
publish`, which does the following in sequence:

* release-ready - Make sure the working directory is clean, we don't depend
  on snapshots, and we haven't already tagged this release.
* version-to-stable - Strip the snapshot version.
* publish-local
* publish
* git-commit - Check in our version number changes.
* git-tag - Add a tag for the current version.
* version-bump-patch - Bump our version by 1.
* version-to-snapshot - Restore the version to snapshot.
* git-commit - Check in these changes.
