package com.ludwig.taglessfinal.testing.itv.news

import com.ludwig.taglessfinal.testing.itv.news.Model.ArticleEvent

trait ProduceEvent[F[_]] {
  def produce(event: ArticleEvent): F[Unit]
}

object ProduceEvent {
  def apply[F[_]](implicit pe: ProduceEvent[F]): ProduceEvent[F] = pe
}
