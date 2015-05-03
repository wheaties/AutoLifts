import AutoLift._

lazy val autoz = build("autolift", "autoz").settings(
  libraryDependencies ++= Seq(
  	"org.scalaz" %% "scalaz-core" % ScalaZ,
  	"org.scalatest" %% "scalatest" % "2.2.1" % "test")
)