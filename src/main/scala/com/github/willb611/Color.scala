package com.github.willb611

import scala.util.Random

object Color {
  val GREEN: Color = new Color("GREEN", Console.GREEN)
  val RED: Color = new Color("RED", Console.RED)
  val YELLOW: Color = new Color("YELLOW", Console.YELLOW)
  val BLUE: Color = new Color("BLUE", Console.BLUE)
  val MAGENTA: Color = new Color("MAGENTA", Console.MAGENTA)
  val CYAN: Color = new Color("CYAN", Console.CYAN)
  val RESET: Color = new Color("RESET", Console.RESET)

  val ansiColors: List[Color] = List(RED, GREEN, YELLOW,
      BLUE, MAGENTA, CYAN)
  val randomGenerator: Random = new Random

  def randomColor(): Color = {
    var index: Int = randomGenerator.nextInt(ansiColors.length)
    ansiColors(index)
  }
}

class Color(val hexVal: String, val ansiCode: String) {
  def this(hex: String) = this(hex, "")
  def this() = this("FFFFFF")

  override def toString(): String = {
    hexVal
  }
}