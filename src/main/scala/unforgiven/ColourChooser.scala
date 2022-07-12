package unforgiven

import scala.util.Random

class ColourChooser {
  val colours: Array[Colour] = Colour.values
  given Array[Colour] = colours

  def choose(using choices: Array[Colour]): Colour =
    Random.shuffle(choices.toList).head}
