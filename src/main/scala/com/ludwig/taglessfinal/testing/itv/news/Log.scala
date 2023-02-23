package com.ludwig.taglessfinal.testing.itv.news

trait Log[F[_]] {
  def info(msg: String): F[Unit]
}

object Log {
  def apply[F[_]](implicit logger: Log[F]): Log[F] = logger
}