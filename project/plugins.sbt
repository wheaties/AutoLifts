resolvers += Resolver.url(
  "tpolecat-sbt-plugin-releases",
    url("http://dl.bintray.com/content/tpolecat/sbt-plugin-releases"))(
        Resolver.ivyStylePatterns)

resolvers += Classpaths.sbtPluginReleases

resolvers += Resolver.sonatypeRepo("releases")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")    

addSbtPlugin("org.tpolecat" % "tut-plugin" % "0.5.2")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.8.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.4")

addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.3.3")

addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.2.16")

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3")