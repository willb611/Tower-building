package com.github.willb611

import com.github.willb611.EnvironmentEffects.EnvironmentEffect

object Environment {
  var Default = new Environment
}

class Environment {
  def applyAnyEffects(worker: Worker): Boolean = {
    // do nothing to workers
    false
  }

  def applyAnyEffects(tower: Tower): Boolean = {
    tower.environmentEffect(EnvironmentEffects.Lightning)
    true
  }
}