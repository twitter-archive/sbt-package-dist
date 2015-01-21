import com.twitter.sbt._

version := "0.1"

scalaVersion := "2.10.4"

seq(GitProject.gitSettings:_*)

seq(BuildProperties.newSettings:_*)
