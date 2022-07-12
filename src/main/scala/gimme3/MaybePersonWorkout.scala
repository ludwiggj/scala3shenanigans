package gimme3

import model.Person
import model.Person.{listPeople, peeps, personOrdering}

object MaybePersonWorkout {

  val maybePeeps = Seq(Some(peeps(0)), None, Some(peeps(1)), None, Some(peeps(2)))

  def sortThings[T](things: Seq[T])(using Ordering[T]): Seq[T] =
    things.sorted

  given optionOrdering[T](using normalOrdering: Ordering[T]): Ordering[Option[T]] with {
    override def compare(a: Option[T], b: Option[T]): Int = (a, b) match {
      case (None, None) => 0
      case (None, _) => -1
      case (_, None) => 1
      case (Some(a), Some(b)) => normalOrdering.compare(a, b)
    }
  }

  def getMap(using size: Int): Map[String, Int] = Map("Alice" -> 5, "Bob" -> 2)

  def main(args: Array[String]): Unit = {
    println(peeps)
    println(sortThings(peeps))

    println(maybePeeps)
    println(sortThings(maybePeeps))

    implicit val a = 3
    // Not sure how to declare a given for an Int
    // given impInt: Int with 25

    getMap("Alice")
    getMap(using 12)("Bob")
  }
}