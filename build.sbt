import sbt.Keys._
import sbt.Project.projectToRef

// a special crossProject for configuring a JS/JVM/shared structure
lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(
    scalaVersion := Settings.versions.scala
  )
  // set up settings specific to the JS project
  .jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJVM = shared.jvm.settings(name := "sharedJVM")

lazy val sharedJS = shared.js.settings(name := "sharedJS")

// use eliding to drop some debug code in the production build
lazy val elideOptions = settingKey[Seq[String]]("Set limit for elidable functions")

// instantiate the JS project for SBT with some additional settings
lazy val client: Project = (project in file("client"))
  .settings(
    name := "client",
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    libraryDependencies ++= Settings.scalajsDependencies.value,
    // by default we do development build, no eliding
    elideOptions := Seq(),
    scalacOptions ++= elideOptions.value,
    jsDependencies ++= Settings.jsDependencies.value,
    // RuntimeDOM is needed for tests
    jsDependencies += RuntimeDOM % "test",
    // yes, we want to package JS dependencies
    skip in packageJSDependencies := false,
    // use Scala.js provided launcher code to start the client app
    persistLauncher := true,
    persistLauncher in Test := false,
    // use uTest framework for tests
    testFrameworks += new TestFramework("utest.runner.Framework")
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .dependsOn(sharedJS)

// Client projects (just one in this case)
lazy val clients = Seq(client)

// Command for building a release
lazy val ReleaseCmd = Command.command("release") {
  state => "set elideOptions in client := Seq(\"-Xelide-below\", \"WARNING\")" ::
    "client/clean" ::
    "client/test" ::
    "set elideOptions in client := Seq()" ::
    state
}
