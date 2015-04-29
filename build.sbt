import AutoLift._

lazy val autoz = build("autolift", "autoz").settings(
  libraryDependencies += "org.scalaz" %% "scalaz-core" % ScalaZ
)