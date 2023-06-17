import sbt._

//noinspection SpellCheckingInspection
object Dependencies {
  val cats = "org.typelevel" %% "cats-core" % "2.9.0"
  val newtype = "io.estatico" %% "newtype" % "0.4.4"

  val backendDeps = Seq(cats, newtype)
}
