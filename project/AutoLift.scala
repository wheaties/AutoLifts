import sbt._
import sbt.Keys._
import xerial.sbt.Sonatype._
import xerial.sbt.Sonatype.SonatypeKeys._

object AutoLift{
	val ScalaVersion = "2.11.7"
	val ScalaZ = "7.2.0"

  def build(pjName: String, base: String) = Project(
    id = pjName,
    base = file(base),
    settings = sonatypeSettings ++
      Seq(
        scalaVersion := ScalaVersion,
        name := pjName,
        organization := "com.github.wheaties",
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
          "-Ywarn-value-discard",
          "-Xfuture",
          "-Ywarn-unused-import"),
        scalacOptions in (Compile, console) ~= { defaultOptions =>
          val unwantedOptions = Set("-Ywarn-unused-import", "-Xfatal-warnings")
          defaultOptions filterNot unwantedOptions
        },
        libraryDependencies ++= Seq(
          "org.scalatest" %% "scalatest" % "2.2.1" % "test"
        ),
        pomExtra := autoliftPom,
        sonatypeProfileName := "wheaties",
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
        publishArtifact in Test := false,
        resolvers ++= Seq(
          Resolver.sonatypeRepo("releases"),
          Resolver.sonatypeRepo("snapshots")
        )
      )
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

