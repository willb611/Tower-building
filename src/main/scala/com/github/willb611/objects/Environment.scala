package com.github.willb611.objects

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import com.github.willb611.messages.Command
import com.github.willb611.objects.Environment.{ActorJoinEnvironmentAdvisory, ApplyEffectCommand, ApplyEffectsToActors, EnvironmentEffectTimerKey}
import com.github.willb611.objects.EnvironmentEffects.EnvironmentEffect

import scala.concurrent.duration.{FiniteDuration, _}
import scala.util.Random

object Environment {
  def props(random: Random, effectInterval: FiniteDuration): Props = Props(new Environment(random, effectInterval))

  val DefaultEffectInterval: FiniteDuration = 100 milliseconds
  private object EnvironmentEffectTimerKey
  // Messages
  case class ActorJoinEnvironmentAdvisory(actor: ActorRef)
  case class ApplyEffectCommand(environmentEffect: EnvironmentEffect) extends Command
  final case object ApplyEffectsToActors extends Command
}

class Environment(val random: Random, val effectInterval: FiniteDuration)
  extends Actor
    with Timers
    with ActorLogging {
  def this() = this(new Random, Environment.DefaultEffectInterval)
  var actorsToEffect: Set[ActorRef] = Set()

  {
    timers.startPeriodicTimer(EnvironmentEffectTimerKey, ApplyEffectsToActors, effectInterval)
  }

  def applyAnyEffects(): Unit = {
    actorsToEffect.foreach(applyAnyEffects)
  }

  def nextEffect(): EnvironmentEffect = EnvironmentEffects.Lightning

  private def applyAnyEffects(actorAffectedByEnvironment: ActorRef): Boolean = {
    val applyEffect = random.nextBoolean()
    if (applyEffect) {
      val effect = nextEffect()
      actorAffectedByEnvironment ! ApplyEffectCommand(effect)
    }
    applyEffect
  }

  override def receive = {
    case message: ActorJoinEnvironmentAdvisory =>
      actorsToEffect += message.actor
    case ApplyEffectsToActors =>
      applyAnyEffects()
  }
}