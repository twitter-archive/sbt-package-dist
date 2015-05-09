import sbt.Keys._
import sbt._

object StandardProjectPlugin extends Build {

  val publishSettings : Seq[Def.Setting[_]] = Seq(
    publishMavenStyle := false,
    bintray.BintrayKeys.bintrayOrganization := Some("websudos"),
    bintray.BintrayKeys.bintrayRepository := "oss-releases",
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => true},
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0"))
  )

  val mavenPublishSettings : Seq[Def.Setting[_]] = Seq(
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    publishMavenStyle := true,
    publishTo <<= version.apply {
      v =>
        val nexus = "https://oss.sonatype.org/"
        if (v.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => true },
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0")),
    pomExtra :=
      <url>https://github.com/websudos/sbt-package-dist</url>
        <scm>
          <url>git@github.com:websudos/sbt-package-dist.git</url>
          <connection>scm:git:git@github.com:websudos/sbt-package-dist.git</connection>
        </scm>
        <developers>
          <developer>
            <id>alexflav</id>
            <name>Flavian Alexandru</name>
            <url>http://github.com/alexflav23</url>
          </developer>
        </developers>
  )

  lazy val root = Project(
    id = "sbt-package-dist",
    base = file("."),
    settings = 
      ScriptedPlugin.scriptedSettings ++
      mavenPublishSettings
  ).settings(
    organization := "com.websudos",
    name := "sbt-package-dist",
    version := "1.2.0",
    scalaVersion := "2.10.5",
    sbtPlugin := true,
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked"
    ),
    libraryDependencies ++= Seq (
      "ivysvn" % "ivysvn" % "2.1.0",
      "org.markdownj" % "markdownj" % "0.3.0-1.0.2b4",
      "org.freemarker" % "freemarker" % "2.3.16"
    )
  )
}
