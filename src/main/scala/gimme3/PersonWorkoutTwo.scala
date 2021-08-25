package gimme3

import model.Person
import model.Person.{listPeople, peeps, given Ordering[Person]}

object PersonWorkoutTwo {
  
  def main(args: Array[String]): Unit =
    println(peeps)
    println(listPeople(peeps))
}