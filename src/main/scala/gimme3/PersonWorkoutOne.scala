package gimme3

import model.Person.{peeps, listPeople, personOrdering}

object PersonWorkoutOne {

  def main(args: Array[String]): Unit =
    println(peeps)
    println(listPeople(peeps))
}