package com.github.willb611

import java.util.concurrent.TimeoutException

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.github.willb611.ColorCollectionHelper.CountOfColors
import com.github.willb611.GameHost.WinningColorQuery
import com.github.willb611.humans.BuilderCoordinator
import com.github.willb611.messages.Query
import com.github.willb611.objects.{Environment, TowerSpace}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}

object GameHost {
  case class WinningColorQuery(maxTimeout: Timeout) extends Query
}

class GameHost(gameConfig: GameConfig) extends Actor with ActorLogging {
  private val environment: ActorRef = context.actorOf(Props[Environment])
  private val coordinators: ListBuffer[ActorRef] = ListBuffer()
  private val towerSpaces: ListBuffer[ActorRef] = ListBuffer()

  override def preStart(): Unit = {
    log.info("[preStart] Entered method.")
    var colors = Color.ansiColors
    for (_ <- 0 until gameConfig.coordinators) {
      if (colors.isEmpty) {
        colors = Color.ansiColors
      }
      val c = Color.randomColor(colors)
      colors = colors.filter(filtered => filtered != c)
      coordinators += context.actorOf(BuilderCoordinator.props(gameConfig.buildersPerCoordinator, c))
    }
    for (_ <- 0 until gameConfig.spacesForTowers) {
      towerSpaces += context.actorOf(TowerSpace.props(environment, gameConfig.towersPerSpace), "towerSpace")
    }
    super.preStart()
    log.debug("[preStart] complete!")
  }

  override def receive: Receive = {
    case winnerRequest: WinningColorQuery =>
      log.debug("[receive] Got WinningColorQuery")
      sender() ! currentlyWinningColor(winnerRequest.maxTimeout)
  }

  def currentlyWinningColor(timeout: Timeout): Option[Color] = {
    val colorCountMap: CountOfColors = buildCountOfTowerColors(timeout)
    val result = colorCountMap.highestPercentColor()
    log.info("[currentlyWinningColor] got {}", result)
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
      val futureResult: Future[CountOfColors] = ask(towerSpace, TowerSpace.CountOfTowersWithColorRequest())(timeout).mapTo[CountOfColors]
      Await.result(futureResult, timeout.duration)
    } catch {
      case e: TimeoutException =>
        log.error("[countOfTowersWithColor] Couldn't get color count from {}, timeout set to {}. Error: {}", towerSpace, timeout, e)
        CountOfColors.EmptyCount
    }
  }
}
