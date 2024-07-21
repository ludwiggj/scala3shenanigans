package com.ludwig.redis4cats

import cats.effect._
import cats.implicits._
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.effect.Log.Stdout._

// https://redis4cats.profunktor.dev/quickstart.html
object QuickStart extends IOApp.Simple {
  def run: IO[Unit] =
    Redis[IO].utf8("redis://localhost:6379").use { redis =>
      for {
        _ <- redis.set("foo", "123")
        x <- redis.get("foo")
        _ <- redis.setNx("foo", "should not happen")
        y <- redis.get("foo")
        _ <- IO(println(x === y)) // true
      } yield ()
    }
}