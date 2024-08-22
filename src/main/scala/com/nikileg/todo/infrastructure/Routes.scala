package com.nikileg.todo.infrastructure

import cats.effect._
import com.comcast.ip4s._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.jdk.CollectionConverters._

object Routes {
  def helloRoute = HttpRoutes.of[IO] {
    case GET -> Root / "healthz" =>
      Ok(s"Im alright!")
    case GET -> Root =>
      Ok("Hello, World!")
  }

  private def portFromEnv = System.getenv().asScala.get("PORT").flatMap(Port.fromString)

  def httpApp = Router("/" -> helloRoute).orNotFound

  def server = {
    Resource.eval(IO.delay(Slf4jLogger.getLoggerFromClass[IO](Routes.getClass))).flatMap { logger =>
      EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(portFromEnv.getOrElse(port"8080"))
        .withHttpApp(httpApp)
        .build
        .evalTap(s => IO.println(s"Listening on ${s.address.getHostName}:${s.address.getPort}"))
    }
  }
}