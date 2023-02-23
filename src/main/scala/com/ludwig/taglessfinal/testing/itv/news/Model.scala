package com.ludwig.taglessfinal.testing.itv.news

object Model {
  final case class Article(id: String, title: String, topic: String)

  final case class ArticleEvent(article: Article, relatedArticles: List[Article])

  final case class ArticleNotFound(id: String) extends Throwable
}
