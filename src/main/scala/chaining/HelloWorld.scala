package chaining

import cats.effect.unsafe.implicits.global
import cats.effect.{Async, IO}
import cats.syntax.all.*
import scala.language.postfixOps
import org.typelevel.log4cats.slf4j.Slf4jLogger.getLoggerFromName

object HelloWorld {
  def logMessagesOnConsecutiveLines[F[_] : Async](): F[Unit] = {
    val logger = getLoggerFromName[F](HelloWorld.getClass.getSimpleName)

    logger.info("****** hello 1") >>
      logger.info("****** hello 2") >>
      logger.info("****** hello 3") >>
      logger.info("****** hello 4")
  }

  def logMessagesWithInterspersedBlankLines[F[_] : Async](): F[Unit] = {
    val logger = getLoggerFromName[F](HelloWorld.getClass.getSimpleName)

    logger.info("****** hello 5") >>

    logger.info("****** hello 6") >>

    logger.info("****** hello 7") >>
      logger.info("****** hello 8")
  }

  def hello1(n: Int): IO[Unit] = IO.println(s"Hello, World! ($n)")

  def hello2(n: Int): IO[Unit] = IO.println("Hello") flatMap { _ => IO.println(s"World! (flatMap)($n)")}

  def hello3(n: Int): IO[Unit] = for {
    _ <- IO.println("Hello")
    _ <- IO.println(s"World! (for)($n)")
  } yield ()

  def hello4(n: Int): IO[Unit] = IO.println("Hello") >> IO.println(s"World! (>>)($n)")

  def IOPrintlinesOnConsecutiveLines(): IO[Unit] = {
    IO.println("Hi 1") >>
      IO.println("Hi 2") >>
      IO.println("Hi 3") >>
      IO.println("Hi 4")
  }

  def IOPrintlinesWithInterspersedBlankLines(): IO[Unit] = {
    IO.println("Hi 5") >>

    IO.println("Hi 6") >>
      IO.println("Hi 7") >>
      IO.println("Hi 8")
  }

  def main(args: Array[String]): Unit = {
    logMessagesOnConsecutiveLines[IO]().unsafeRunSync()
    logMessagesWithInterspersedBlankLines[IO]().unsafeRunSync()
    IOPrintlinesOnConsecutiveLines().unsafeRunSync()
    IOPrintlinesWithInterspersedBlankLines().unsafeRunSync()
  }
}
