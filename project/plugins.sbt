sbtResolver <<= (sbtResolver) { r =>
  Option(System.getenv("SBT_PROXY_REPO")) map { x =>
    Resolver.url("proxy repo for sbt", url(x))(Resolver.ivyStylePatterns)
  } getOrElse r
}

resolvers ++= Seq(
  "twitter.com" at "http://maven.twttr.com/",
  "scala-tools" at "http://scala-tools.org/repo-releases/",
  "maven" at "http://repo1.maven.org/maven2/",
  "freemarker" at "http://freemarker.sourceforge.net/maven2/"
)

externalResolvers <<= resolvers map identity

libraryDependencies <+= sbtVersion { sv =>
  "org.scala-sbt" % "scripted-plugin" % sv
}

libraryDependencies += "ivysvn" % "ivysvn" % "2.1.0"

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")