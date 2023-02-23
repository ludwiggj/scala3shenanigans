package com.ludwig.taglessfinal.testing.itv.news

import cats.data.{EitherT, Writer, WriterT}
import cats.implicits.{catsSyntaxEitherId, catsSyntaxOptionId, none, toFunctorOps}
import cats.instances.either._
import com.ludwig.taglessfinal.testing.itv.news.Event.{ArticleFetched, ContentfulCalled, EventProduced}
import com.ludwig.taglessfinal.testing.itv.news.Model.{Article, ArticleEvent, ArticleNotFound}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

// NOTE: Complete version of this code is at:
//       https://github.com/ludwiggj/scala-scratch/tree/master/src/main/scala/org/ludwiggj/taglessfinal/testing/itv/news
//       This excerpt is here to show use of type lambdas
class ProcessArticleSpec extends AnyFlatSpec with Matchers {
  val articleId: String = "20230120_001"
  val article: Article = Article(id = articleId, title = "How to test tagless final", topic = "Testing")
  val expectedArticleEvent: ArticleEvent = ArticleEvent(article, Nil)

  trait HappyPathFixture[E] {
    // Instead of:
    // type EventWriter[A] = Writer[List[Event], A]

    // We can write:
    type EventWriter = [X] =>> Writer[List[Event], X]

    // These are type lambdas, new for Scala 3
    // See also:
    //  https://docs.scala-lang.org/scala3/new-in-scala3.html
    //  https://blog.rockthejvm.com/scala-3-type-lambdas/
    type F[A] = EitherT[EventWriter, E, A]

    val expectedEvents: List[Event] = List(
      ArticleFetched(articleId), ContentfulCalled(article.topic), EventProduced(expectedArticleEvent)
    )

    def logEventAndValue[A](l: Event, a: A): EitherT[EventWriter, E, A] =
      EitherT.liftF(Writer.tell(List(l)).as(a))

    implicit val log: Log[F] = _ =>
      EitherT.pure(())

    implicit val repo: ArticleRepo[F] = id =>
      logEventAndValue(ArticleFetched(id), article.some)

    //noinspection NotImplementedCode
    implicit val contentfulClient: ContentfulClient[F] = topic =>
      logEventAndValue(ContentfulCalled(topic), Nil)

    //noinspection NotImplementedCode
    implicit val eventProducer: ProduceEvent[F] = articleEvent =>
      logEventAndValue(EventProduced(articleEvent), ())
  }

  behavior of "Process Article (article present)"

  it should "Return article" in new HappyPathFixture[ArticleNotFound] {
    ProcessArticle.mkTake[F].process(articleId).value.run shouldEqual(expectedEvents, expectedArticleEvent.asRight)
  }
}
