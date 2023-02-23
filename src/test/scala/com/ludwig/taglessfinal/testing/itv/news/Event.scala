package com.ludwig.taglessfinal.testing.itv.news

import com.ludwig.taglessfinal.testing.itv.news.Model.ArticleEvent

sealed trait Event

object Event {
  case class ArticleFetched(id: String) extends Event

  case class ContentfulCalled(topic: String) extends Event

  case class EventProduced(event: ArticleEvent) extends Event
}