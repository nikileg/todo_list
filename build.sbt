import Dependencies._

ThisBuild / organization := "com.nikileg"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "todo_list",
    libraryDependencies ++= backendDeps,
    Compile / run / fork := true
  )