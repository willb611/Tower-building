package com.github.willb611

object Color {
  val GREEN: Color = new Color("green")
  val RED: Color = new Color("red")
}

class Color(val hexVal: String) {
  def this() = this("FFFFFF")
  override def toString(): String = {
    hexVal
  }
}