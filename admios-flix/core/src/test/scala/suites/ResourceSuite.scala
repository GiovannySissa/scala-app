package suites

import cats.effect.concurrent.Deferred
import cats.effect.{ContextShift, IO, Resource, Timer}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.ExecutionContext

trait ResourceSuite[R] extends TestSuite with BeforeAndAfterAll {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)

  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  def resources: Resource[IO, R]

  private[this] var resource: R = _
  private[this] var clean: IO[Unit] = _

  private[this] val l = Deferred[IO, Unit].unsafeRunSync()

  override def beforeAll(): Unit = {
    super.beforeAll()
    val (r, h) = resources.allocated.unsafeRunSync()
    resource = r
    clean    = h
    l.complete(()).unsafeRunSync()
  }

  override def afterAll(): Unit = {
    clean.unsafeRunSync()
    super.afterAll()
  }

  def withResource(f: (=> R) => Unit): Unit = f {
    l.get.unsafeRunSync()

    resource
  }

}
