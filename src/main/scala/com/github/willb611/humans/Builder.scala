package com.github.willb611.humans

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.actor.Timers
import com.github.willb611.Color
import com.github.willb611.humans.Builder._
import com.github.willb611.messages.Command
import com.github.willb611.objects.Environment.ApplyEffect
import com.github.willb611.objects.EnvironmentEffects
import com.github.willb611.objects.EnvironmentEffects.EnvironmentEffect
import com.github.willb611.objects.Tower.AddBlockRequest

import scala.concurrent.duration._

object Builder {
  val DefaultBlockPlacementIntervalMillis: FiniteDuration = 100 milliseconds
  //  private object DoWorkTimerKey
  final case class TowerToBuild(towerActor: ActorRef)
  case class DoWork() extends Command
}

class Builder(val colorToUseForBlocks: Color) extends Actor
  with ActorLogging with Timers {
  private var tower: Option[ActorRef] = None

  var activeEffects: List[EnvironmentEffect] = List()
//
//  def scheduleRegularWork(workInterval: FiniteDuration): Unit = {
//    timers.startPeriodicTimer(DoWorkTimerKey, DoWork, workInterval)
//  }

  private def doWork(time: Int): Unit = {
    if (activeEffects.isEmpty) {
      buildForTime(time)
    } else {
      log.debug(s"$this not placing block as affected by $activeEffects")
      if (activeEffects.contains(EnvironmentEffects.Lightning)) {
        activeEffects = activeEffects.dropWhile(_ == EnvironmentEffects.Lightning)
      }
    }
  }

  private def buildForTime(time: Int) {
    for (_ <- 0 until time) {
      if (tower.isDefined) {
        tower.get ! AddBlockRequest(colorToUseForBlocks)
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
      tower = Option(msg.towerActor)
    case DoWork =>
      doWork(1)
    case effect: ApplyEffect =>
      environmentEffect(effect.environmentEffect)
  }

  override def unhandled(message: Any): Unit = {
    log.info("[unhandled] Ignoring message: {}", message)
    super.unhandled(message)
  }
}