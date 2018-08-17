package com.github.willb611.builders

import akka.actor.{Actor, ActorLogging, ActorRef, Props, SupervisorStrategy, Timers}
import com.github.willb611.GameHost.{TowerSpacesQuery, TowerSpacesResponse}
import com.github.willb611.{Color, RandomHelper, RestartKilledSupervisionStrategy}
import com.github.willb611.builders.Builder.{DoWork, TowerToBuild, TowerToBuildQuery}
import com.github.willb611.builders.BuilderCoordinator._
import com.github.willb611.messages._
import com.github.willb611.objects.TowerSpace.TowersInSpaceQuery

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.{FiniteDuration, _}
import scala.language.postfixOps

object BuilderCoordinator {
  val ActorNamePrefix: String = "coordinator"

  val BuilderWorkInterval: FiniteDuration = 200 millis
  private object WorkCommandTimerKey
  def props(buildersToCreate: Int, color: Color): Props = Props(new BuilderCoordinator(buildersToCreate, color))

  // Messages
  final case object BuildersBeingCoordinatedQuery extends Query
  final case class TowerListResponse(towers: List[ActorRef]) extends Response
  final case class BuilderListResponse(builders: List[ActorRef]) extends Response
  private final case object AskBuildersToWork extends Request
}

class BuilderCoordinator(buildersToCreate: Int, color: Color)
  extends Actor
    with ActorLogging
    with Timers
    with UnhandledMessagesLogged {
  override val supervisorStrategy: SupervisorStrategy = RestartKilledSupervisionStrategy(super.supervisorStrategy).strategy
  private val builderNameIterator = Iterator from 1 map (i => s"${Builder.ActorNamePrefix}-$i")
  var builders: ListBuffer[ActorRef] = ListBuffer()
  var towers: Set[ActorRef] = Set()

  override def preStart(): Unit = {
    for (_ <- 0 until buildersToCreate) {
      val builder = context.actorOf(Builder.props(color), builderNameIterator.next())
      builders += builder
      log.debug(s"[preStart] Created builder $builder")
    }
    timers.startPeriodicTimer(WorkCommandTimerKey, AskBuildersToWork, BuilderWorkInterval)
    context.parent ! TowerSpacesQuery
    super.preStart()
  }

  def updateBuilders(): Unit = {
    if (towers.nonEmpty) {
      builders.foreach(builder => {
        val tower = RandomHelper.randomFrom(towers).get
        builder ! TowerToBuild(tower)
        log.debug(s"[updateBuilders] forwarded $tower onto $builder")
      })
    } else {
      log.warning(s"[updateBuilders] $towers is empty")
    }
  }

  override def receive = {
    case msg: TowerSpacesResponse =>
      log.debug(s"[receive] got $msg")
      msg.towerSpaces.foreach(t => t ! TowersInSpaceQuery)
    case towerListMsg: TowerListResponse =>
      log.debug(s"[receive] Got : $towerListMsg")
      towerListMsg.towers.foreach(ref => {
        towers = towers + ref
      })
      updateBuilders()
    case AskBuildersToWork =>
      log.debug("[receive] Prompting builders to work..")
      builders.foreach(b => b ! DoWork)
    // Queries
    case TowerToBuildQuery =>
      if (towers.nonEmpty) {
        updateBuilders()
      }
    case BuildersBeingCoordinatedQuery =>
      sender() ! BuilderListResponse(builders.toList)
  }
}
