package com.github.willb611

import com.github.willb611.EnvironmentEffects.EnvironmentEffect

object Worker {
  private var instancesMadeSoFar: Int = 0

  def nextColor(): Color = {
    new Color
  }
  def nextId(): Int = {
    var cpy = instancesMadeSoFar
    instancesMadeSoFar += 1
    cpy
  }
}

class Worker(val tower: Tower, val colorToUseForBlocks: Color) {
  def this(tower: Tower) = this(tower, Color.randomColor())
  private val id = Worker.nextId()

  var activeEffects: List[EnvironmentEffect] = List()

  def doWork(time: Int): Unit = {
    if (activeEffects.isEmpty) {
      buildForTime(time)
    } else {
      if (activeEffects.contains(EnvironmentEffects.Lightning)) {
        activeEffects = activeEffects.dropWhile(_ == EnvironmentEffects.Lightning)
      }
    }
  }

  def buildForTime(time: Int) {
    for (_ <- 0 until time) {
      tower.addBlock(colorToUseForBlocks)
    }
  }

  def environmentEffect(effect: EnvironmentEffect) {
    activeEffects = effect :: activeEffects
  }

  override def toString(): String = {
    "[Worker] " + id + " color: " + colorToUseForBlocks
  }
}
