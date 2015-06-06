import AutoLift._

lazy val autoz = build("autolift", "autoz").settings(
  libraryDependencies ++= Seq(
    "org.scalaz" %% "scalaz-core" % ScalaZ,
    "org.scalatest" %% "scalatest" % "2.2.1" % "test"),
  sonatypeProfileName := "wheaties",
  wartremoverErrors in (Compile, compile) ++= Warts.allBut(Wart.Var, Wart.NoNeedForMonad)
)

scalaVersion := AutoLift.ScalaVersion