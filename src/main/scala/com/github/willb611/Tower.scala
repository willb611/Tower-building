package com.github.willb611


class Tower() {
  val defaultColor = "FFFFFF"
  var blocks: List[String] = List()

  def height():Int = blocks.length
  def lastColor():String = blocks.head

  def addBlock(str: String): Tower = {
    blocks = List(str) ++ blocks
    this
  }
  def addBlock(): Tower = {
    addBlock(defaultColor)
    this
  }
}
