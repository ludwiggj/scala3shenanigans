package logging.example2

import cats.effect.IO
import Types.{RIO, EntityB, EntityBId}
import cats.effect.unsafe.implicits.global

// see https://docs.scala-lang.org/scala3/reference/contextual/context-functions.html
// and https://www.scala-lang.org/blog/2016/12/07/implicit-function-types.html

object TraceableExample2 {
  given cid: CorrelationId = CorrelationId("XZUP-8890-1234-IOAW-2222")

  trait ServiceA {
    def createA(a: EntityA)(using CorrelationId): RIO[EntityB]
  }

  trait ClientB {
    def getB(bId: EntityBId)(using CorrelationId): RIO[EntityB]
  }

  private val clientB = new ClientB {
    override def getB(bId: EntityBId)(using CorrelationId): RIO[EntityB] = {
      val value = s"($bId,EntityB)"
      for {
        result <- IO.pure[EntityB](value)
        _ <- Logger.info(s"getB [$bId] Result [$value]")
      }
      yield result
    }
  }

  case class ServiceC(clientB: ClientB) extends ServiceA {
    def somePrivateBusinessLogic(entityB: EntityB)(using CorrelationId): RIO[EntityB] =
      for {
        result <- IO.pure[EntityB]({
          val intermediate = entityB.split(",")
          s"(${intermediate(1)},${intermediate(0)})"
        })
        _ <- Logger.info(s"somePrivateBusinessLogic [$entityB] Result [$result]")
      }
      yield result

    override def createA(a: EntityA)(using CorrelationId): RIO[EntityB] =
      for {
        entityB <- clientB.getB(a.idOfB)
        processed <- somePrivateBusinessLogic(entityB)
        _ <- Logger.info(s"createA entity [${a.idOfB}] Result [$processed]")
      }
      yield processed
  }

  def main(args: Array[String]): Unit = {
    val rio = ServiceC(clientB).createA(EntityA("991"))

    println(s"Result = ${rio.unsafeRunSync()}")
  }
}
