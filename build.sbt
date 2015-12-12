import AutoLift._
import com.typesafe.sbt.SbtSite.SiteKeys._
import com.typesafe.sbt.SbtGhPages.GhPagesKeys._
import sbtunidoc.Plugin.UnidocKeys._

lazy val core = build("autolift-core", "autolift-core").settings(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "export-hook" % "1.1.1-SNAPSHOT",
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    compilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full),
    "org.scalatest" %% "scalatest" % "2.2.1" % "test"
  ),
  sourceGenerators in Compile <+= (sourceManaged in Compile).map(Boilerplate.genCore),
  sonatypeProfileName := "wheaties"
)

lazy val autoAlge = build("autolift-algebird", "autolift-algebird").settings(
  libraryDependencies ++= Seq(
    "com.twitter" %% "algebird-core" % "0.11.0",
    "com.twitter" %% "algebird-util" % "0.11.0",
    "com.twitter" %% "algebird-test" % "0.11.0" % "test", //check if actually needed
    compilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full),
    "org.scalatest" %% "scalatest" % "2.2.1" % "test"
  ),
  sourceGenerators in Compile <+= (sourceManaged in Compile).map(Boilerplate.genAlgebird),
  sonatypeProfileName := "wheaties"
)
.dependsOn(core)

lazy val autoScalaz = build("autolift-scalaz", "autolift-scalaz").settings(
  libraryDependencies ++= Seq(
    "org.scalaz" %% "scalaz-core" % ScalaZ,
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    compilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full),
    "org.scalatest" %% "scalatest" % "2.2.1" % "test"
  ),
  sourceGenerators in Compile <+= (sourceManaged in Compile).map(Boilerplate.genScalaz),
  sonatypeProfileName := "wheaties"
)
.dependsOn(core)

lazy val docs = build("docs", "docs")
  .settings(tutSettings: _*)
  .settings(site.settings: _*)
  .settings(ghpages.settings: _*)
  .settings(
    publishArtifact := false,
    site.addMappingsToSiteDir(tut, "_tut"),
    site.addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), "latest/api"),
    unidocProjectFilter in (ScalaUnidoc, unidoc) := inAnyProject,
    ghpagesNoJekyll := false,
    git.remoteRepo := "git@github.com:wheaties/autolifts.git",
    autoAPIMappings := true,
    includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.md"
  )
  .settings(site.includeScaladoc(): _*)
  .settings(site.jekyllSupport(): _*)
  .settings(unidocSettings: _*)
  .dependsOn(core, autoAlge, autoScalaz)

lazy val bench = build("bench", "bench")
  .settings(
    publishArtifact := false
  )
  .dependsOn(core, autoScalaz)
  .enablePlugins(JmhPlugin)


scalaVersion := AutoLift.ScalaVersion

addCommandAlias("gen-site", "unidoc;tut;make-site")