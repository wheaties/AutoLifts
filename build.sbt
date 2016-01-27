import AutoLift._
import com.typesafe.sbt.SbtSite.SiteKeys._
import com.typesafe.sbt.SbtGhPages.GhPagesKeys._
import sbtunidoc.Plugin.UnidocKeys._

lazy val root = (project in file(".")).settings(
  scalaVersion := AutoLift.ScalaVersion,
  publishArtifact := false
)
.aggregate(core, autoAlge, autoScalaz, autoCats, docs)

lazy val core = build("autolift-core", "autolift-core").settings(
  sourceGenerators in Compile <+= (sourceManaged in Compile).map(Boilerplate.genCore)
)

lazy val autoCats = build("autolift-cats", "autolift-cats").settings(
  libraryDependencies ++= Seq(
    "org.spire-math" %% "cats" % "0.3.0",
    compilerPlugin("org.spire-math" %% "kind-projector" % "0.6.3")
  ),
  sourceGenerators in Compile <+= (sourceManaged in Compile).map(Boilerplate.genCats)
)
  .dependsOn(core)

lazy val autoAlge = build("autolift-algebird", "autolift-algebird").settings(
  libraryDependencies ++= Seq(
    "com.twitter" %% "algebird-core" % "0.11.0",
    compilerPlugin("org.spire-math" %% "kind-projector" % "0.6.3")
  ),
  sourceGenerators in Compile <+= (sourceManaged in Compile).map(Boilerplate.genAlgebird)
)
.dependsOn(core)

lazy val autoScalaz = build("autolift-scalaz", "autolift-scalaz").settings(
  libraryDependencies ++= Seq(
    "org.scalaz" %% "scalaz-core" % ScalaZ,
    compilerPlugin("org.spire-math" %% "kind-projector" % "0.6.3")
  ),
  initialCommands in console := """
    import scalaz._
    import scalaz.Scalaz._
    import autolift.scalaz._
    import autolift.Scalaz._
  """,
  sourceGenerators in Compile <+= (sourceManaged in Compile).map(Boilerplate.genScalaz)
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

addCommandAlias("gen-site", "unidoc;tut;make-site")

