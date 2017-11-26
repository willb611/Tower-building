package com.github.willb611

import java.util.concurrent.TimeoutException

import akka.actor.{Actor, ActorLogging, ActorRef, Props, SupervisorStrategy}
import akka.pattern.ask
import akka.util.Timeout
import com.github.willb611.GameHost._
import com.github.willb611.builders.BuilderCoordinator
import com.github.willb611.builders.BuilderCoordinator.TowerSpaceAdvisory
import ColorCollectionHelper.CountOfColors
import com.github.willb611.messages.Query
import com.github.willb611.objects.{Environment, TowerSpace}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}

object GameHost {
  val ActorName: String = "gameHost"
  def props(gameConfig: GameConfig): Props = Props(new GameHost(gameConfig))

        // Messages
  case class WinningColorQuery(maxTimeout: Timeout) extends Query
  object TowerSpacesQuery extends Query
  object BuilderCoordinatorsQuery extends Query
  // Response
  case class BuilderCoordinatorsAdvisory(coordinators: List[ActorRef])
  case class TowerSpacesAdvisory(towerSpaces: List[ActorRef])
}

class GameHost(gameConfig: GameConfig) extends Actor with ActorLogging {
  override val supervisorStrategy: SupervisorStrategy = RestartKilledSupervisionStrategy(super.supervisorStrategy).strategy
  private val environment: ActorRef = context.actorOf(Props[Environment])

  private val coordinators: ListBuffer[ActorRef] = ListBuffer()
  private val coordinatorNameIter = Iterator from 1 map (i => s"${BuilderCoordinator.ActorNamePrefix}-$i")

  private val towerSpaces: ListBuffer[ActorRef] = ListBuffer()
  private val towerSpaceNameIter = Iterator from 1 map (i => s"${TowerSpace.ActorNamePrefix}-$i")

  private var chaosMonkey: Option[ActorRef] = None

  private def makeCoordinators(): Unit = {
    var colors = Color.ansiColors
    for (_ <- 0 until gameConfig.coordinatorCount) {
      if (colors.isEmpty) {
        colors = Color.ansiColors
      }
      val c = Color.randomColor(colors)
      colors = colors.filter(filtered => filtered != c)
      coordinators += context.actorOf(BuilderCoordinator.props(gameConfig.buildersPerCoordinator, c), coordinatorNameIter.next())
    }
  }

  private def makeTowerSpaces(): Unit = {
    for (_ <- 0 until gameConfig.towerSpaceCount) {
      val towerSpace = context.actorOf(TowerSpace.props(environment, gameConfig.towersPerSpace), towerSpaceNameIter.next())
      towerSpaces += towerSpace
    }
  }

  def makeChaosMonkey() = {
    chaosMonkey = Some(context.actorOf(Props[ChaosMonkey], ChaosMonkey.ActorName))
  }

  override def preStart(): Unit = {
    log.info("[preStart] Setting up game.")
    makeCoordinators()
    makeTowerSpaces()
    makeChaosMonkey()
    log.info("[preStart] Setup complete!")
    super.preStart()
  }

  override def receive: Receive = {
    case winnerRequest: WinningColorQuery =>
      log.debug("[receive] Got WinningColorQuery")
      sender() ! currentlyWinningColor(winnerRequest.maxTimeout)
    // queries
    case TowerSpacesQuery =>
      val response = TowerSpacesAdvisory(towerSpaces.toList)
      log.debug(s"[receive] Responding to $TowerSpacesQuery with $response")
      sender() ! response
    case BuilderCoordinatorsQuery =>
      sender() ! BuilderCoordinatorsAdvisory(coordinators.toList)
  }

  def currentlyWinningColor(timeout: Timeout): Option[Color] = {
    val colorCountMap: CountOfColors = buildCountOfTowerColors(timeout)
    val result = colorCountMap.highestPercentColor()
    log.info(s"[currentlyWinningColor] From $colorCountMap got $result")
    result
  }

  private def buildCountOfTowerColors(timeout: Timeout): CountOfColors = {
    var countOfColors = CountOfColors.EmptyCount
    for (towerSpace <- towerSpaces) {
      val countFromSpace = countOfTowersWithColor(towerSpace, timeout)
      countOfColors = countOfColors.updateWith(countFromSpace)
    }
    countOfColors
  }

  def countOfTowersWithColor(towerSpace: ActorRef, timeout: Timeout): CountOfColors = {
    try {
      val query = TowerSpace.CountOfTowersWithColorQuery
      log.info(s"[countOfTowersWithColor] Sending query $query to $towerSpace")
      val futureResult: Future[CountOfColors] = ask(towerSpace, query)(timeout).mapTo[CountOfColors]
      Await.result(futureResult, timeout.duration)
    } catch {
      case e: TimeoutException =>
        log.error("[countOfTowersWithColor] Couldn't get color count from {}, timeout set to {}. Error: {}", towerSpace, timeout, e)
        CountOfColors.EmptyCount
    }
  }
}
