package com.github.willb611

import java.util.concurrent.TimeoutException

import akka.actor.{Actor, ActorLogging, ActorRef, Props, SupervisorStrategy}
import akka.pattern.ask
import akka.util.Timeout
import com.github.willb611.GameHost._
import com.github.willb611.builders.BuilderCoordinator
import ColorCollectionHelper.CountOfColors
import com.github.willb611.messages.GenericMessages.{StateQuery, WinningColorQuery, defaultMessageTimeout}
import com.github.willb611.messages.{Query, Response}
import com.github.willb611.objects.{Environment, TowerSpace, TowerSpaceState}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

object GameHost {
  val ActorName: String = "gameHost"
  def props(gameConfig: GameConfig): Props = Props(new GameHost(gameConfig))

  // Messages
  object TowerSpacesQuery extends Query
  object BuilderCoordinatorsQuery extends Query
  // Responses
  case class BuilderCoordinatorsResponse(coordinators: List[ActorRef]) extends Response
  case class TowerSpacesResponse(towerSpaces: List[ActorRef]) extends Response
}

class GameHost(gameConfig: GameConfig)
  extends Actor
    with ActorLogging
    with UnhandledMessagesLogged {
  override val supervisorStrategy: SupervisorStrategy = RestartKilledSupervisionStrategy(super.supervisorStrategy).strategy
  private val environment: ActorRef = context.actorOf(Props[Environment])

  private val coordinators: ListBuffer[ActorRef] = ListBuffer()
  private val coordinatorNameIter = Iterator from 1 map (i => s"${BuilderCoordinator.ActorNamePrefix}-$i")

  private val towerSpaces: ListBuffer[ActorRef] = ListBuffer()
  private val towerSpaceNameIter = Iterator from 1 map (i => s"${TowerSpace.ActorNamePrefix}-$i")

  private var chaosMonkey: Option[ActorRef] = None

  private def makeCoordinators(): ListBuffer[ActorRef] = {
    var colors = Color.ansiColors
    val result: ListBuffer[ActorRef] = ListBuffer()
    for (_ <- 0 until gameConfig.coordinatorCount) {
      if (colors.isEmpty) {
        colors = Color.ansiColors
      }
      val c = Color.randomColor(colors)
      colors = colors.filter(filtered => filtered != c)
      result += context.actorOf(BuilderCoordinator.props(gameConfig.buildersPerCoordinator, c), coordinatorNameIter.next())
    }
    result
  }

  private def makeTowerSpaces(): ListBuffer[ActorRef] = {
    val result: ListBuffer[ActorRef] = ListBuffer()
    for (_ <- 0 until gameConfig.towerSpaceCount) {
      val towerSpace = context.actorOf(TowerSpace.props(environment, gameConfig.towersPerSpace), towerSpaceNameIter.next())
      result += towerSpace
    }
    result
  }

  private def makeChaosMonkey(): ActorRef = {
    context.actorOf(ChaosMonkey.props(), ChaosMonkey.ActorName)
  }

  override def preStart(): Unit = {
    log.info("[preStart] Setting up game.")
    coordinators ++= makeCoordinators()
    towerSpaces ++= makeTowerSpaces()
    chaosMonkey = Some(makeChaosMonkey())
    log.info("[preStart] Setup complete!")
    super.preStart()
  }

  override def receive: Receive = {
    case winnerRequest: WinningColorQuery =>
      log.debug("[receive] Got WinningColorQuery")
      sender() ! currentlyWinningColor(winnerRequest.maxTimeout)
    // queries
    case TowerSpacesQuery =>
      val response = TowerSpacesResponse(towerSpaces.toList)
      log.debug(s"[receive] Responding to $TowerSpacesQuery with $response")
      sender() ! response
    case BuilderCoordinatorsQuery =>
      sender() ! BuilderCoordinatorsResponse(coordinators.toList)
    case StateQuery =>
      sender() ! gameState()
  }

  def currentlyWinningColor(timeout: Timeout): Option[Color] = {
    val colorCountMap: CountOfColors = buildCountOfTowerColors(timeout)
    val result = colorCountMap.highestPercentColor()
    log.info(s"[currentlyWinningColor] From $colorCountMap got $result")
    result
  }

  def gameState(): GameState = {
    var states: ListBuffer[TowerSpaceState] = ListBuffer()
    for (towerSpace <- towerSpaces) {
      val futureResponse = ask(towerSpace, StateQuery)(defaultMessageTimeout).mapTo[TowerSpaceState]
      states += Await.result(futureResponse, defaultMessageTimeout.duration)
    }
    GameState(states.toList)
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
