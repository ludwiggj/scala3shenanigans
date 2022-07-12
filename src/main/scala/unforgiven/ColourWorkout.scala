package unforgiven

object ColourWorkout {

  def chooseRainbowColours(): Unit = {
    val colourChooser = new ColourChooser
    import colourChooser.given_Array_Colour
    for (_ <- 1 until 10)
      println(colourChooser.choose)
  }

  def chooseGBColours(): Unit = {
    val colourChooser = new ColourChooserGB
    import colourChooser.given_Array_Colour
    for (_ <- 1 until 10)
      println(colourChooser.choose)
  }

  def main(args: Array[String]): Unit = {
    println("Rainbow!\n")
    chooseRainbowColours()
    println("\nGB!\n")
    chooseGBColours()
  }
}