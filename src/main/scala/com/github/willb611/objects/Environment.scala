package com.github.willb611.objects

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.github.willb611.messages.Command
import com.github.willb611.objects.Environment._
import com.github.willb611.objects.EnvironmentEffects.EnvironmentEffect

import scala.util.Random

object Environment {
  case class InEnvironment(actor: ActorRef)
  case class ApplyEffect(environmentEffect: EnvironmentEffect) extends Command
}

class Environment(val random: Random) extends Actor with ActorLogging {
  import EnvironmentEffects._
  def this() = this(new Random)
  var actorsToEffect: Set[ActorRef] = Set()

  def applyAnyEffects(): Unit = {
    actorsToEffect.foreach(applyAnyEffects)
  }

  def nextEffect(): EnvironmentEffect = EnvironmentEffects.Lightning

  private def applyAnyEffects(actorAffectedByEnvironment: ActorRef): Boolean = {
    val applyEffect = random.nextBoolean()
    if (applyEffect) {
      val effect = nextEffect()
      log.debug(s"About to apply affect $effect to $actorAffectedByEnvironment")
      actorAffectedByEnvironment ! ApplyEffect(effect)
    }
    applyEffect
  }

  override def receive = {
    case message: InEnvironment =>
      actorsToEffect += message.actor
  }
}