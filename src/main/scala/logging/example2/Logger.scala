package logging.example2

import cats.effect.IO
import Types.RIO

object Logger:
  def info(msg: String)(using CorrelationId): RIO[Unit] =
    IO(println(s"[${summon[CorrelationId].id}] > $msg"))