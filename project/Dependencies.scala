import sbt._

//noinspection SpellCheckingInspection,TypeAnnotation
object Dependencies {
  val http4sVersion = "0.23.27"
  val doobieVersion = "1.0.0-RC4"

  val cats = "org.typelevel" %% "cats-core" % "2.12.0"
  val newtype = "io.estatico" %% "newtype" % "0.4.4"

  val logDeps = Seq(
    "org.typelevel" %% "log4cats-core" % "2.7.0",
    "org.typelevel" %% "log4cats-slf4j" % "2.7.0",
    "org.slf4j" % "slf4j-simple" % "2.0.13",
  )

  val doobieDeps = Seq(
    "org.tpolecat" %% "doobie-core" % doobieVersion,
    "org.tpolecat" %% "doobie-hikari" % doobieVersion, // HikariCP transactor.
    "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  )

  private val http4sDeps = Seq(
    "org.http4s" %% "http4s-ember-client" % http4sVersion,
    "org.http4s" %% "http4s-ember-server" % http4sVersion,
    "org.http4s" %% "http4s-dsl" % http4sVersion,
    "org.http4s" %% "http4s-core" % http4sVersion,
    "org.http4s" %% "http4s-client" % http4sVersion,
    "org.http4s" %% "http4s-server" % http4sVersion,
  )

  val backendDeps = Seq(cats, newtype) ++
    logDeps ++
    http4sDeps ++
    doobieDeps
}
