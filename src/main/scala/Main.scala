import cats.effect.{ExitCode, IO, IOApp}
import com.nikileg.todo.infrastructure.Routes

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = Routes.server[IO].useForever.as(ExitCode.Success)
}
