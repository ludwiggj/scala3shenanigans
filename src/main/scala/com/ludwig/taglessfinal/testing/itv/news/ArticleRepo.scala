package com.ludwig.taglessfinal.testing.itv.news

import com.ludwig.taglessfinal.testing.itv.news.Model.Article

trait ArticleRepo[F[_]] {
  def get(id: String): F[Option[Article]]
}

object ArticleRepo {
  def apply[F[_]](implicit ar: ArticleRepo[F]): ArticleRepo[F] = ar
}
