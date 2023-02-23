package com.ludwig.taglessfinal.testing.itv.news

import com.ludwig.taglessfinal.testing.itv.news.Model.Article

trait ContentfulClient[F[_]] {
  def articlesByTopic(topic: String): F[List[Article]]
}

object ContentfulClient {
  def apply[F[_]](implicit cc: ContentfulClient[F]): ContentfulClient[F] = cc
}
