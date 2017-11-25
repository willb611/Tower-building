package com.github.willb611

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.github.willb611.GameHost.WinningColorQuery
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object TowerGame extends App with LazyLogging {
  override def main(args: Array[String]): Unit = {
    logger.info("[main] Hello world")
    val actorSystem: ActorSystem = ActorSystem("TowerGame")
    val gameHost: ActorRef = actorSystem.actorOf(GameHost.props(GameConfig()), GameHost.ActorName)
    Thread.sleep(4000)
    logCurrentWinner(gameHost)
    actorSystem.terminate()
  }

  def requestCurrentWinner(gameHost: ActorRef, query: WinningColorQuery): Option[Color] = {
    try {
      val timeout = query.maxTimeout
      logger.debug("[logCurrentWinner] Using gameHost: {}, timeout: {}, query: {}", gameHost, timeout, query)
      logger.info(s"[logCurrentWinner] Querying gameHost $gameHost for the winner")
      val gameHostResponse: Future[Any] = gameHost.ask(query)(timeout)
      val resultAsTypeAny: Any = Await.result(gameHostResponse, timeout.duration)
      resultAsTypeAny.asInstanceOf[Option[Color]]
    } catch {
      case e: Exception =>
        logger.error(s"[requestCurrentWinner] Error: ${e.getMessage}, returning None", e)
        None
    }
  }

  private def logCurrentWinner(gameHost: ActorRef): Unit = {
    val timeout = Timeout(1 minute)
    val query: WinningColorQuery = WinningColorQuery(timeout)
    val resultOption = requestCurrentWinner(gameHost, query)
    if (resultOption.isDefined) {
      val result = resultOption.get
      logger.info(s"${result.ansiCode}[logCurrentWinner] Got winning color as: $result${Color.RESET.ansiCode}")
    } else {
      logger.info("[logCurrentWinner] No winning color defined!")
    }
  }
}

case class GameConfig() {
  val coordinators: Int = 5
  val buildersPerCoordinator: Int = 15
  val spacesForTowers: Int = 1
  val towersPerSpace: Int = 5
}