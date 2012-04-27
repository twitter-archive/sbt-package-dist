import sbt._
import Keys._

import com.twitter.sbt._

import java.io.{File, FileReader}
import java.util.Properties

import fm.last.ivy.plugins.svnresolver.SvnResolver

object StandardProjectPlugin extends Build {
  lazy val root = Project(
    id = "sbt-package-dist",
    base = file("."),
    settings = StandardProject.newSettings ++
      SubversionPublisher.newSettings ++
      ScriptedPlugin.scriptedSettings
  ).settings(
    organization := "com.twitter",
    name := "sbt-package-dist",
    version := "1.0.5",
    SubversionPublisher.subversionRepository := Some("https://svn.twitter.biz/maven-public"),
    sbtPlugin := true,
    libraryDependencies ++= Seq (
      "ivysvn" % "ivysvn" % "2.1.0",
      "org.markdownj" % "markdownj" % "0.3.0-1.0.2b4",
      "org.freemarker" % "freemarker" % "2.3.16"
    )
  )
}
