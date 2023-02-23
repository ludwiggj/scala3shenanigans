package com.ludwig.taglessfinal.testing.itv.news

import cats.syntax.all.*
import cats.MonadError
import com.ludwig.taglessfinal.testing.itv.news.Model.{ArticleEvent, ArticleNotFound}

trait ProcessArticle[F[_]] {
  def process(id: String): F[ArticleEvent]
}

object ProcessArticle {
    type ArticleError[F[_]] = MonadError[F, ArticleNotFound]

    def mkTake[F[_] : ArticleError : Log : ArticleRepo : ContentfulClient : ProduceEvent]: ProcessArticle[F] = (id: String) => for {
      maybeArticle <- ArticleRepo[F].get(id)
      article <- maybeArticle match {
        case Some(anArticle) => anArticle.pure
        case None => ArticleNotFound(id).raiseError
      }
      relatedArticles <- ContentfulClient[F].articlesByTopic(article.topic)
      event = ArticleEvent(article, relatedArticles.filterNot(_.id == article.id))
      _ <- ProduceEvent[F].produce(event)
    } yield event
}
