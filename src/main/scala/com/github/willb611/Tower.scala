package com.github.willb611

import com.github.willb611.EnvironmentEffects.EnvironmentEffect

object Tower {
  val DEFAULT_COLOR: Color = new Color("FFFFFF")

  private var instancesMadeSoFar: Int = 0;
  def nextId(): Int = {
    var cpy = instancesMadeSoFar
    instancesMadeSoFar += 1
    cpy
  }
}

class Tower() {
  val id = Tower.nextId()
  var blocks: List[Color] = List()

  def height(): Int = blocks.length
  def lastColor(): Option[Color] = {
    if (blocks.length > 0) {
      Some(blocks.head)
    } else {
      None
    }
  }

  def addBlock(str: Color): Tower = {
    blocks = List(str) ++ blocks
    this
  }
  def addBlock(): Tower = {
    addBlock(Tower.DEFAULT_COLOR)
  }

  def environmentEffect(effect: EnvironmentEffect) = {
    blocks = blocks.drop(1)
  }

  override def toString(): String = {
    "[Tower] " + id + " h: " + height + ", lastColor: " + lastColor
  }
}
