package com.github.willb611.builders

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import com.github.willb611.Color
import com.github.willb611.builders.Builder.{DoWork, TowerBeingBuiltQuery, TowerToBuild, TowerToBuildQuery}
import com.github.willb611.messages.{Command, Query}
import com.github.willb611.objects.Environment.ApplyEffectCommand
import com.github.willb611.objects.EnvironmentEffects
import com.github.willb611.objects.EnvironmentEffects.EnvironmentEffect
import com.github.willb611.objects.Tower.AddBlockRequest

object Builder {
  def props(color: Color): Props = Props(new Builder(color))
  val ActorNamePrefix: String = "builder"
  // Messages
  final case class TowerToBuild(towerActor: ActorRef) extends Command
  final case object TowerToBuildQuery extends Query
  final case object DoWork extends Command
  final case object TowerBeingBuiltQuery extends Query
}

class Builder(val colorToUseForBlocks: Color) extends Actor
  with ActorLogging with Timers {
  private var tower: Option[ActorRef] = None

  var activeEffects: List[EnvironmentEffect] = List()

  override def preStart(): Unit = {
    context.parent ! TowerToBuildQuery
    super.preStart()
  }

  private def doWork(time: Int): Unit = {
    if (activeEffects.isEmpty) {
      buildForTime(time)
    } else {
      if (activeEffects.contains(EnvironmentEffects.Lightning)) {
        activeEffects = activeEffects.dropWhile(_ == EnvironmentEffects.Lightning)
      }
    }
  }

  private def buildForTime(time: Int) {
    for (_ <- 0 until time) {
      if (tower.isDefined) {
        tower.get ! AddBlockRequest(colorToUseForBlocks)
      } else {
        log.warning("[buildForTime] No tower found!")
      }
    }
  }

  private def environmentEffect(effect: EnvironmentEffect) {
    activeEffects = effect :: activeEffects
  }

  override def toString: String = {
    s"[Builder]{color=$colorToUseForBlocks}"
  }

  override def receive = {
    case msg: TowerToBuild =>
      log.debug(s"[receive] Given tower: $msg")
      tower = Option(msg.towerActor)
    case DoWork =>
      doWork(1)
    case effect: ApplyEffectCommand =>
      environmentEffect(effect.environmentEffect)
    // queries
    case TowerBeingBuiltQuery =>
      log.debug(s"[receive] Responding to $TowerBeingBuiltQuery with $tower")
      sender() ! tower
  }
}