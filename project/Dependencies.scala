import sbt._

//noinspection SpellCheckingInspection,TypeAnnotation
object Dependencies {
  val cats = "org.typelevel" %% "cats-core" % "2.9.0"
  val newtype = "io.estatico" %% "newtype" % "0.4.4"

  val http4sVersion = "0.23.27"

  private val http4sDeps = Seq(
    "org.http4s" %% "http4s-ember-client" % http4sVersion,
    "org.http4s" %% "http4s-ember-server" % http4sVersion,
    "org.http4s" %% "http4s-dsl"          % http4sVersion,
    "org.http4s" %% "http4s-core"         % http4sVersion,
    "org.http4s" %% "http4s-client"       % http4sVersion,
    "org.http4s" %% "http4s-server"       % http4sVersion,
  )

  val backendDeps = Seq(cats, newtype) ++
    http4sDeps
}
