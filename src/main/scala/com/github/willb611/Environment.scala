package com.github.willb611

import com.typesafe.scalalogging.LazyLogging

import scala.util.Random

object Environment {
  var Default = new Environment
}

class Environment(val random: Random) extends LazyLogging {
  def this() = this(new Random)

  def applyAnyEffects(builder: Builder): Boolean = {
    val applyEffect = random.nextBoolean()
    if (applyEffect) {
      val effect = EnvironmentEffects.Lightning
      logger.debug(s"$builder, will be affected by $effect")
      builder.environmentEffect(effect)
    }
    applyEffect
  }

  def applyAnyEffects(tower: Tower): Boolean = {
    val applyEffect = random.nextBoolean()
    if (applyEffect) {
      val effect = EnvironmentEffects.Lightning
      logger.debug(s"$tower, will be affected by $effect")
      tower.environmentEffect(effect)
    }
    applyEffect
  }
}

object EnvironmentEffects {
  sealed trait EnvironmentEffect
  case object Lightning extends EnvironmentEffect
}
