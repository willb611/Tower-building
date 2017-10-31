package com.github.willb611

object EnvironmentEffects {
  sealed trait EnvironmentEffect
  case object Lightning extends EnvironmentEffect
}
