package com.ludwig.redis4cats

import cats.effect.IO
import com.dimafeng.testcontainers.{ForAllTestContainer, GenericContainer}
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.effect.Log.Stdout.*
import org.scalatest.wordspec.AnyWordSpecLike
import org.testcontainers.containers.wait.strategy.Wait
import cats.effect.unsafe.implicits.global

class RedisSpec extends AnyWordSpecLike with ForAllTestContainer {
  val redisPort                            = 6379
  override val container: GenericContainer = GenericContainer(
    "redis:7.0.4",
    exposedPorts = Seq(redisPort),
    waitStrategy = Wait.forListeningPort()
  )

  lazy val redisHost: String = container.host
  lazy val mappedPort: Int   = container.mappedPort(redisPort)
  lazy val redisLocation     = s"redis://$redisHost:$mappedPort"

  "Respect TTL for NowNext schedule response" in {
    Redis[IO].utf8(redisLocation).use { redis =>
      for {
        _ <- redis.set("foo", "123")
        x <- redis.get("foo")
        _ <- redis.setNx("foo", "should not happen")
        y <- redis.get("foo")
        _ <- IO(println(x === y)) // true
        _ <- IO(println(x)) // true
      } yield ()
    }.unsafeRunSync()
  }
}