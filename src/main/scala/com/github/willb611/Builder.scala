package com.github.willb611

import com.github.willb611.EnvironmentEffects.EnvironmentEffect
import com.typesafe.scalalogging.LazyLogging

object Builder {
  private var instancesMadeSoFar: Int = 0

  def nextColor(): Color = {
    new Color
  }
  def nextId(): Int = {
    val cpy = instancesMadeSoFar
    instancesMadeSoFar += 1
    cpy
  }
}

class Builder(val towers: List[Tower], val colorToUseForBlocks: Color) extends LazyLogging {
  def this(towers: List[Tower]) = this(towers, Color.randomColor())
  def this(tower: Tower) = this(tower :: List())
  private val id = Builder.nextId()

  var activeEffects: List[EnvironmentEffect] = List()

  def doWork(time: Int): Unit = {
    if (activeEffects.isEmpty) {
      buildForTime(time)
    } else {
      logger.debug(s"$this not placing block as affected by $activeEffects")
      if (activeEffects.contains(EnvironmentEffects.Lightning)) {
        activeEffects = activeEffects.dropWhile(_ == EnvironmentEffects.Lightning)
      }
    }
  }

  def buildForTime(time: Int) {
    for (_ <- 0 until time) {
      towers.head.addBlock(colorToUseForBlocks)
    }
  }

  def environmentEffect(effect: EnvironmentEffect) {
    activeEffects = effect :: activeEffects
  }

  override def toString: String = {
    "[Builder] " + id + " color: " + colorToUseForBlocks
  }
}
