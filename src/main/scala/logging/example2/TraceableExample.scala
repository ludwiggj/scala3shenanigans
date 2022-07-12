package logging.example2

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import logging.example2.Types.{EntityB, EntityBId, RIO}

// see https://docs.scala-lang.org/scala3/reference/contextual/context-functions.html
// and https://www.scala-lang.org/blog/2016/12/07/implicit-function-types.html

object TraceableExample {
  given cid: CorrelationId = CorrelationId("XZUP-8890-1234-IOAW")
  
  trait ServiceA {
    def createA(a: EntityA): RIO[EntityB]
  }

  trait ClientB {
    def getB(bId: EntityBId): RIO[EntityB]
  }

  private val clientB = new ClientB {
    override def getB(bId: EntityBId): RIO[EntityB] = {
      val value = s"($bId,EntityB)"
      for {
        result <- IO.pure[EntityB](value)
        _ <- Logger.info(s"getB [$bId] Result [$value]")
      }
      yield result
    }
  }

  case class ServiceC(clientB: ClientB) extends ServiceA {
    def somePrivateBusinessLogic(entityB: EntityB): RIO[EntityB] =
      for {
        result <- IO.pure[EntityB]({
          val intermediate = entityB.split(",")
          s"(${intermediate(1)},${intermediate(0)})"
        })
        _ <- Logger.info(s"somePrivateBusinessLogic [$entityB] Result [$result]")
      }
      yield result

    override def createA(a: EntityA): RIO[EntityB] =
      for {
        entityB <- clientB.getB(a.idOfB)
        processed <- somePrivateBusinessLogic(entityB)
        _ <- Logger.info(s"createA entity [${a.idOfB}] Result [$processed]")
      }
      yield processed
  }

  def main(args: Array[String]): Unit = {
    val rio = ServiceC(clientB).createA(EntityA("991"))

    println(s"Result = ${rio.unsafeRunSync()}")}
