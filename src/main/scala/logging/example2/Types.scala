package logging.example2

import cats.effect.IO

object Types:
  type EntityB = String
  type EntityBId = String
  type Traceable[T] = CorrelationId ?=> T
  type RIO[T] = Traceable[IO[T]]