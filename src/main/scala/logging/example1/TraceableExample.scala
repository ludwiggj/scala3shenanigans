package logging.example1

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global

// This example makes use of context function type
// See https://docs.scala-lang.org/scala3/reference/contextual/context-functions.html
// https://www.scala-lang.org/blog/2016/12/07/implicit-function-types.html
object TraceableExample {

  type Traceable[T] = CorrelationId ?=> T

  type RIO[T] = Traceable[Future[T]]

  def main(args: Array[String]): Unit = {
    given cid: CorrelationId = CorrelationId("correlationId-123")

    val future: RIO[User] = (for {
      user <- createUser("hello", "world")
      _ <- Logger.info(s"Created user ${user.email}")
      email <- sendEmail(user)
      _ <- Logger.info(s"Sent email $email to user ${user.email}")
    }
    yield user)

    val result = Await.result(future, 10.seconds)
  }
  end main

  case class CorrelationId(value: String) extends AnyVal

  case class User(name: String, email: String)

  case class Email(id: String)

  def createUser(name: String, email: String): RIO[User] =
    Future.successful(User(name, email))

  def sendEmail(user: User): RIO[Email] =
    Future.successful(Email("id-123"))

  object Logger {
    def info(msg: String): RIO[Unit] =
      Future.successful(println(s"[${summon[CorrelationId].value}] - $msg"))
  }
}
