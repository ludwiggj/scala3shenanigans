package model

case class Person(surname: String, name: String, age: Int) {
  def greet: String = s"Hey, I'm $name. Scala rocks!"
}

object Person {

  // given doesn't have to be named
  given personOrdering: Ordering[Person] with {
    override def compare(x: Person, y: Person): Int =
      x.surname.compareTo(y.surname)
  }

  val peeps = Seq(
    Person("Ludwig", "Matt", 19),
    Person("Smith", "Hannah", 19),
    Person("Edwards", "Alex", 23)
  )

  def listPeople(persons: Seq[Person])(using Ordering[Person]): Seq[Person] =
    persons.sorted

  def main(args: Array[String]): Unit = {
    println(peeps)
    println(listPeople(peeps))

    given stringToPerson: Conversion[String, Person] with {
      def apply(s: String): Person = Person(s, s, 15)
    }

    import scala.language.implicitConversions

    println("Alice".greet)
  }
}