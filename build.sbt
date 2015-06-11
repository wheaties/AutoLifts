import AutoLift._

lazy val autoz = build("autolift", "autoz").settings(
  libraryDependencies ++= Seq(
    "org.scalaz" %% "scalaz-core" % ScalaZ,
    "org.scalatest" %% "scalatest" % "2.2.1" % "test"),
  sonatypeProfileName := "wheaties",
  wartremoverErrors in (Compile, compile) ++= Warts.allBut(Wart.Var, Wart.NoNeedForMonad)
)

lazy val docs = build("docs", "docs")
  .settings(tutSettings: _*)
  .settings(publishArtifact := false)
  .dependsOn(autoz)

scalaVersion := AutoLift.ScalaVersion