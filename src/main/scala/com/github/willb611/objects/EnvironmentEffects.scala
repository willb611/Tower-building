package com.github.willb611.objects

object EnvironmentEffects {
  sealed trait EnvironmentEffect
  case object Lightning extends EnvironmentEffect
}
