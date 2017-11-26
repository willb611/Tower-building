package com.github.willb611

import com.typesafe.scalalogging.LazyLogging

object Color extends LazyLogging {
  val GREEN: Color = new Color("GREEN", Console.GREEN)
  val RED: Color = new Color("RED", Console.RED)
  val YELLOW: Color = new Color("YELLOW", Console.YELLOW)
  val BLUE: Color = new Color("BLUE", Console.BLUE)
  val MAGENTA: Color = new Color("MAGENTA", Console.MAGENTA)
  val CYAN: Color = new Color("CYAN", Console.CYAN)
  val RESET: Color = new Color("RESET", Console.RESET)

  val ansiColors: List[Color] = List(RED, GREEN, YELLOW,
      BLUE, MAGENTA, CYAN)

  def randomColor(): Color = {
    randomColor(ansiColors)
  }
  def randomColor(colors: List[Color]): Color = {
    try {
      RandomHelper.randomFrom(colors).get
    } catch {
      case e: Exception =>
        logger.error("[randomColor] Error! {}", e)
        Color.RESET
    }
  }
}

class Color(val description: String, val ansiCode: String) {
  def this(description: String) = this(description, "")
  def this() = this("FFFFFF")

  override def toString: String = {
    ansiCode + description + Color.RESET.ansiCode
  }
}