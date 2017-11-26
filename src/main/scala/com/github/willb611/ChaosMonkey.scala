package com.github.willb611

import akka.actor.{Actor, ActorLogging, ActorRef, Kill, Props, Timers}
import com.github.willb611.ChaosMonkey._
import com.github.willb611.GameHost.{BuilderCoordinatorsAdvisory, BuilderCoordinatorsQuery, TowerSpacesAdvisory, TowerSpacesQuery}
import com.github.willb611.builders.BuilderCoordinator.TowerListAdvisory
import com.github.willb611.messages.Command
import com.github.willb611.objects.TowerSpace.TowersInSpaceQuery

import scala.concurrent.duration.{FiniteDuration, _}
import scala.util.Random

object ChaosMonkey {
  val ActorName: String = "chaosMonkey"

  def props(random: Random): Props = Props(new ChaosMonkey(random))
  final case object ChaosTimerKey
  final val IntervalForChaos: FiniteDuration = 1000 millis
  final case object QueryForVictimsTimerKey
  private final val IntervalToSeekNewVictims: FiniteDuration = 500 millis
  // Messages
  final case object CauseChaos extends Command
  final case object QueryForVictims extends Command
}

class ChaosMonkey(random: Random)
  extends Actor
    with ActorLogging
    with Timers {
  def this() = this(new Random())
  var potentialVictims: Set[ActorRef] = Set()


  override def preStart(): Unit = {
    timers.startPeriodicTimer(ChaosTimerKey, CauseChaos, IntervalForChaos)
    timers.startPeriodicTimer(QueryForVictimsTimerKey, QueryForVictims, IntervalToSeekNewVictims)
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
