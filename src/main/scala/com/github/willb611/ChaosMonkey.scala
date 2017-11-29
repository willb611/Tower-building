package com.github.willb611

import akka.actor.{Actor, ActorLogging, ActorRef, Kill, Props, Timers}
import com.github.willb611.ChaosMonkey._
import com.github.willb611.GameHost.{BuilderCoordinatorsAdvisory, BuilderCoordinatorsQuery, TowerSpacesAdvisory, TowerSpacesQuery}
import com.github.willb611.builders.BuilderCoordinator.TowerListAdvisory
import com.github.willb611.messages.Command
import com.github.willb611.objects.TowerSpace.TowersInSpaceQuery

import scala.concurrent.duration.{FiniteDuration, _}
import scala.language.postfixOps
import scala.util.Random

object ChaosMonkey {
  // config
  private final val DefaultIntervalToPollParentForVictims: FiniteDuration = 500 millis
  private final val DefaultIntervalForChaos: FiniteDuration = 1000 millis
  case class ChaosMonkeyConfig(intervalToSeekNewVictims: FiniteDuration, intervalForChaos: FiniteDuration)
  final val defaultConfig: ChaosMonkeyConfig = {
    ChaosMonkeyConfig(DefaultIntervalToPollParentForVictims, DefaultIntervalForChaos)
  }
  final val ActorName: String = "chaosMonkey"
  // props
  def props(random: Random, config: ChaosMonkeyConfig): Props = {
    Props(new ChaosMonkey(random, config))
  }
  def props(random: Random): Props = Props(new ChaosMonkey(random, defaultConfig))
  def props(): Props = props(new Random)
  // timer stuff
  final case object ChaosTimerKey
  final case object QueryForVictimsTimerKey
  // Messages
  final case object CauseChaos extends Command
  final case object QueryForVictims extends Command
}

class ChaosMonkey(random: Random, config: ChaosMonkeyConfig)
  extends Actor
    with Timers
    with ActorLogging
    with UnhandledMessagesLogged {
  var potentialVictims: Set[ActorRef] = Set()

  override def preStart(): Unit = {
    timers.startPeriodicTimer(ChaosTimerKey, CauseChaos, config.intervalForChaos)
    timers.startPeriodicTimer(QueryForVictimsTimerKey, QueryForVictims, config.intervalToSeekNewVictims)
    super.preStart()
  }

  def selectVictim(): Option[ActorRef] = {
    RandomHelper.randomFrom(potentialVictims.toList)
  }

  def causeChaos(): Unit = {
    val victim: Option[ActorRef] = selectVictim()
    if (victim.isDefined) {
      log.info(s"${Color.RED.ansiCode}[causeChaos] Sending $Kill to $victim${Color.RESET.ansiCode}")
      victim.get ! Kill
    } else {
      log.warning("[causeChaos] No victims found")
    }
  }

  def shouldCauseChaos(): Boolean = random.nextBoolean()

  private def addPotentialVictims(list: List[ActorRef]): Unit = {
    list.foreach(ref => {
      potentialVictims += ref
    })
  }

  override def receive = {
    case QueryForVictims =>
      context.parent ! TowerSpacesQuery
      context.parent ! BuilderCoordinatorsQuery
    case CauseChaos =>
      if (shouldCauseChaos()) {
          log.debug("[receive] Causing chaos..")
          causeChaos()
      } else {
          log.debug("[receive] Lucky this time..")
      }
    // Towers
    case msg: TowerSpacesAdvisory =>
      addPotentialVictims(msg.towerSpaces)
      sender() ! TowersInSpaceQuery
    case msg: TowerListAdvisory =>
      addPotentialVictims(msg.towers)
    // Builders
    case msg: BuilderCoordinatorsAdvisory =>
      addPotentialVictims(msg.coordinators)
//      sender() ! BuildersQuery
//    case BuildersAdvisory =>
//      log.info("TODO")
  }
}
