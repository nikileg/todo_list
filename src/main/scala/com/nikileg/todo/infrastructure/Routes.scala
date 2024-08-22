package com.nikileg.todo.infrastructure

import cats.Show
import cats.effect._
import com.comcast.ip4s._
import com.nikileg.todo.domain.TodoRepository
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.typelevel.log4cats.slf4j.Slf4jLogger
import cats.implicits._
import com.nikileg.todo.domain.model.TodoList.ListId
import doobie.util.transactor.Transactor

import java.util.UUID
import scala.jdk.CollectionConverters._

object Routes {
//  private implicit val listIdShow: Show[ListId] = Show.show(id => s"""<a href="/list/${id.toString}">${id.toString}</a>""")
  def helloRoute = HttpRoutes.of[IO] {
    case GET -> Root / "healthz" =>
      Ok(s"Im alright!")
    case GET -> Root =>
      Ok("Hello, World!")
  }

  def listRoute(service: TodoRepository[IO]) = HttpRoutes.of[IO] {
    case GET -> Root / "list" / id =>
      service
        .getById(UUID.fromString(id))
        .flatMap {
          case Some(list) => Ok(list.show)
          case None => NotFound()
        }
    case GET -> Root / "list" =>
      service.list().flatMap(list => Ok(list.map(_.toString).mkString("\n")))
  }

  private def portFromEnv = System.getenv().asScala.get("PORT").flatMap(Port.fromString)
  private def dbFromEnv: IO[Transactor[IO]] = {
    val env = System.getenv().asScala
    def tx = Transactor.fromDriverManager[IO](
      driver = "org.postgresql.Driver",
      url = env.getOrElse("DB_URL", "jdbc:postgresql://localhost:5432/postgres"),
      user = env.getOrElse("DB_USER", "postgres"),
      password = env.getOrElse("DB_PASSWORD", throw new RuntimeException("DB_PASSWORD not set!")),
      logHandler = None
    )
    IO.delay(tx)
  }

  def httpApp(service: TodoRepository[IO]) = Router("/" -> (helloRoute <+> listRoute(service))).orNotFound

  def server = {
    for {
      logger <- Resource.eval(IO.delay(Slf4jLogger.getLoggerFromClass[IO](Routes.getClass)))
      tx <- Resource.eval(dbFromEnv)
      service <- Resource.pure(TodoRepository.live(tx)(implicitly, implicitly, logger))
      server <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(portFromEnv.getOrElse(port"8080"))
        .withHttpApp(httpApp(service))
        .build
        .evalTap(s => IO.println(s"Listening on ${s.address.getHostName}:${s.address.getPort}"))
    } yield server
  }
}