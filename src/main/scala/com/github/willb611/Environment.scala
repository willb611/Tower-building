package com.github.willb611

import scala.util.Random

object Environment {
  var Default = new Environment
}

class Environment(val random: Random) {
  def this() = this(new Random)

  def applyAnyEffects(worker: Worker): Boolean = {
    val applyEffect = random.nextBoolean()
    if (applyEffect) {
      val effect = EnvironmentEffects.Lightning
      println("[Environment] " + worker + ", will be affected by " + effect)
      worker.environmentEffect(effect)
    }
    applyEffect
  }

  def applyAnyEffects(tower: Tower): Boolean = {
    val applyEffect = random.nextBoolean()
    if (applyEffect) {
      val effect = EnvironmentEffects.Lightning
      println("[Environment] " + tower + ", will be affected by " + effect)
      tower.environmentEffect(effect)
    }
    applyEffect
  }
}

object EnvironmentEffects {
  sealed trait EnvironmentEffect
  case object Lightning extends EnvironmentEffect
}
