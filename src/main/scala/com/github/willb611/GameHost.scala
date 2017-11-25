package com.github.willb611

import java.util.concurrent.TimeoutException

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.github.willb611.ColorCollectionHelper.CountOfColors
import com.github.willb611.GameHost.WinningColorRequest
import com.github.willb611.humans.BuilderCoordinator
import com.github.willb611.objects.{Environment, TowerSpace}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}

object GameHost {
  case class SetupGame(config: GameConfig)
  case class WinningColorRequest(maxTimeout: Timeout)
}

class GameHost(gameConfig: GameConfig) extends Actor with ActorLogging {
  private val environment: ActorRef = context.actorOf(Props[Environment])
  private val coordinators: ListBuffer[ActorRef] = ListBuffer()
  private val towerSpaces: ListBuffer[ActorRef] = ListBuffer()

  override def preStart(): Unit = {
    for (_ <- 0 until gameConfig.coordinators) {
      coordinators += context.actorOf(Props(new BuilderCoordinator(gameConfig.buildersPerCoordinator)))
    }
    for (_ <- 0 until gameConfig.spacesForTowers) {
      towerSpaces += context.actorOf(Props(new TowerSpace(environment, gameConfig.towersPerSpace)))
    }
    super.preStart()
  }

  override def receive: Receive = {
    case winnerRequest: WinningColorRequest =>
      sender() ! currentlyWinningColor(winnerRequest.maxTimeout)
  }

  def currentlyWinningColor(timeout: Timeout): Option[Color] = {
    val colorCountMap: CountOfColors = buildCountOfTowerColors(timeout)
    colorCountMap.highestPercentColor()
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
        log.error("[countOfTowersWithColor] Couldn't get color count from {}, timeout set to {}", towerSpace, timeout)
        CountOfColors.EmptyCount
    }
  }
}
