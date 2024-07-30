package com.nikileg.todo.infrastructure

import cats.Monad
import cats.effect._
import com.comcast.ip4s.{IpLiteralSyntax, Port}
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import scala.jdk.CollectionConverters._

object Routes {
  def helloRoute = HttpRoutes.of[IO] {
    case GET -> Root / "hello" =>
      Ok(s"Hello, World!")
  }

  private def portFromEnv = System.getenv().asScala.get("PORT").flatMap(Port.fromString)
  def httpApp = Router("/" -> helloRoute).orNotFound
  def server = EmberServerBuilder
    .default[IO]
    .withHost(ipv4"0.0.0.0")
    .withPort(portFromEnv.getOrElse(port"8080"))
    .withHttpApp(httpApp)
    .build
}
