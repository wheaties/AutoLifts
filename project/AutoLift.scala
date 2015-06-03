import sbt._
import sbt.Keys._

object AutoLift{
	val ScalaVersion = "2.11.5"
	val ScalaZ = "7.1.1"

	def build(pjName: String, base: String) = Project(id = pjName, base = file(base))
	  .settings(
	    scalaVersion := ScalaVersion,
	    name := pjName,
	    publishArtifact in Test := false,
	    scalacOptions ++= Seq(
        "-deprecation",
        "-encoding", "UTF-8",
        "-feature",
        "-language:higherKinds", 
        "-language:existentials",
        "-unchecked",
        "-Xfatal-warnings",
  		  "-Yno-adapted-args",
  		  "-Ywarn-dead-code",
  		  "-Ywarn-value-discard")
	  )
}