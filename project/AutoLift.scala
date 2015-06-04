import sbt._
import sbt.Keys._

object AutoLift{
	val ScalaVersion = "2.11.5"
	val ScalaZ = "7.1.1"

	def build(pjName: String, base: String) = Project(id = pjName, base = file(base))
	  .settings(
	    scalaVersion := ScalaVersion,
	    name := pjName,
	    scalacOptions := Seq(
        "-deprecation",
        "-encoding", "UTF-8",
        "-feature",
        "-language:higherKinds", 
        "-language:existentials",
        "-unchecked",
        "-Xfatal-warnings",
  		  "-Yno-adapted-args",
  		  "-Ywarn-dead-code",
  		  "-Ywarn-value-discard"),
      pomExtra := autoliftPom,
      publishTo <<= version { v: String =>
        val nexus = "https://oss.sonatype.org/"
        if (v.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "service/local/staging/deploy/maven2")
      },
      credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
      pomIncludeRepository := { x => false },
      publishMavenStyle := true,
      publishArtifact in Test := false
    )

  val autoliftPom =
    <url>http://github.com/wheaties/AutoLifts</url>
    <licenses>
      <license>
        <name>Apache 2</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:wheaties/AutoLifts.git</url>
      <connection>scm:git:git@github.com:wheaties/AutoLifts.git</connection>
    </scm>
    <developers>
      <developer>
           <id>wheaties</id>
           <name>Owein Reese</name>
           <url>www.github.com/wheaties</url>
      </developer>
    </developers>

}