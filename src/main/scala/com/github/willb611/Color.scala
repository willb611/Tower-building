package com.github.willb611

object Color {
  val GREEN: Color = new Color("green", Console.GREEN)
  val RED: Color = new Color("red", Console.RED)

  def randomColor(): Color = {
    Color.RED
  }
}

class Color(val hexVal: String, val ansiCode: String) {
  def this(hex: String) = this(hex, "")
  def this() = this("FFFFFF")

  override def toString(): String = {
    hexVal
  }
}