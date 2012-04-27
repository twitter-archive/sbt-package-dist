package com.twitter.sbt

import sbt._
import Keys._

/**
 * Publish artifacts to an "artifactory" (optionally with credentials) instead of the default
 * publishing target.
 *
 * This mixin only takes effect if `artifactoryResolver` is set.
 */
object ArtifactoryPublisher extends Plugin {
  val artifactoryResolver = SettingKey[Option[Resolver]](
    "artifactory-resolver",
    "resolver for publishing artifacts to artifactory"
  )

  val artifactoryCredentialsFile = SettingKey[Option[File]](
    "artifactory-credentials-file",
    "credentials for publishing artifacts to artifactory"
  )

  val newSettings = Seq(
    artifactoryResolver := None,

    artifactoryCredentialsFile := Some {
      file(System.getProperty("user.home")) / ".artifactory-credentials"
    },

    publishTo <<= (publishTo, artifactoryResolver) { (oldPublish, resolver) =>
      resolver orElse oldPublish
    },

    credentials <<= (credentials, artifactoryCredentialsFile) map { (creds, file) =>
      creds ++ file.map(Credentials(_))
    }
  )
}

/**
 * uses environment variables to pick the right proxy resolver to look at
 */
object DefaultRepos extends Plugin with Environmentalist {
  val defaultResolvers = SettingKey[Seq[Resolver]](
    "default-resolvers",
    "maven repositories to use by default, unless a proxy repo is set via SBT_PROXY_REPO"
  )

  val localRepo = SettingKey[File](
    "local-repo",
    "local folder to use as a repo (and where publish-local publishes to)"
  )

  val newSettings = Seq(
    defaultResolvers := Seq(
      "ibiblio" at "http://mirrors.ibiblio.org/pub/mirrors/maven2/",
      "twitter.com" at "http://maven.twttr.com/",
      "powermock-api" at "http://powermock.googlecode.com/svn/repo/",
      "scala-tools.org" at "http://scala-tools.org/repo-releases/",
      "testing.scala-tools.org" at "http://scala-tools.org/repo-releases/testing/",
      "oauth.net" at "http://oauth.googlecode.com/svn/code/maven",
      "download.java.net" at "http://download.java.net/maven/2/",
      "atlassian" at "https://m2proxy.atlassian.com/repository/public/",
      // for netty:
      "jboss" at "http://repository.jboss.org/nexus/content/groups/public/"
    ),

    localRepo := file(System.getProperty("user.home") + "/.m2/repository"),

    // configure resolvers for the build
    resolvers <<= (
      resolvers,
      defaultResolvers,
      localRepo
    ) { (resolvers, defaultResolvers, localRepo) =>
      (Option(System.getenv("SBT_PROXY_REPO")) map { url =>
        Seq("proxy-repo" at url)
      } getOrElse {
        resolvers ++ defaultResolvers
      }) ++ Seq(
        // the local repo has to be in here twice, because sbt won't push to a "file:"
        // repo, but it won't read artifacts from a "Resolver.file" repo. (head -> desk)
        "local-lookup" at ("file:" + localRepo.getAbsolutePath),
        Resolver.file("local", localRepo)(Resolver.mavenStylePatterns)
      )
    },

    // don't add any special resolvers.
    externalResolvers <<= (resolvers) map identity
  )
}
