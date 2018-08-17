package com.github.willb611

import akka.actor.{Actor, ActorLogging, ActorRef, Kill, Props, Timers}
import com.github.willb611.ChaosMonkey._
import com.github.willb611.GameHost.{BuilderCoordinatorsQuery, BuilderCoordinatorsResponse, TowerSpacesQuery, TowerSpacesResponse}
import com.github.willb611.builders.BuilderCoordinator.{BuilderListResponse, BuildersBeingCoordinatedQuery, TowerListResponse}
import com.github.willb611.messages.{Command, UnhandledMessagesLogged}
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
  private[willb611] final case object ChaosTimerKey
  private[willb611] final case object PollActorsForVictimsTimerKey
  // Messages
  final case object CauseChaos extends Command
  final case object PollActorsForVictimsCommand extends Command
}

class ChaosMonkey(random: Random, config: ChaosMonkeyConfig)
  extends Actor
    with Timers
    with ActorLogging
    with UnhandledMessagesLogged {
  private var potentialVictims: Set[ActorRef] = Set()
  private var towerSpaces: Set[ActorRef] = Set()
  private var towers: Set[ActorRef] = Set()
  private var coordinators: Set[ActorRef] = Set()
  private var builders: Set[ActorRef] = Set()

  override def preStart(): Unit = {
    timers.startPeriodicTimer(ChaosTimerKey, CauseChaos, config.intervalForChaos)
    timers.startPeriodicTimer(PollActorsForVictimsTimerKey, PollActorsForVictimsCommand, config.intervalToSeekNewVictims)
    log.info("[preStart] Causing chaos..")
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
    case PollActorsForVictimsCommand =>
      context.parent ! TowerSpacesQuery
      context.parent ! BuilderCoordinatorsQuery
      towerSpaces.foreach(ts => ts ! TowersInSpaceQuery)
      coordinators.foreach(bc => bc ! BuildersBeingCoordinatedQuery)
    case CauseChaos =>
      if (shouldCauseChaos()) {
          log.debug("[receive] Causing chaos..")
          causeChaos()
      } else {
          log.debug("[receive] Lucky this time..")
      }
    // Tower advisories
    case msg: TowerSpacesResponse =>
      log.debug(s"[receive] Got $msg")
      addPotentialVictims(msg.towerSpaces)
      towerSpaces ++= msg.towerSpaces
    case msg: TowerListResponse =>
      log.debug(s"[receive] Got $msg")
      addPotentialVictims(msg.towers)
      towers ++= msg.towers
    // Builder advisories
    case msg: BuilderCoordinatorsResponse =>
      log.debug(s"[receive] Got $msg")
      addPotentialVictims(msg.coordinators)
      coordinators ++= msg.coordinators
    case msg: BuilderListResponse =>
      log.debug(s"[receive] Got $msg")
      addPotentialVictims(msg.builders)
      builders ++= msg.builders
  }
}
