package com.github.willb611

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.github.willb611.GameHost.WinningColorQuery
import com.github.willb611.api.ApiServer
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

object TowerGame
  extends App
    with LazyLogging {
  final private val sleepTime = 10000
  // main
  logger.info("[main] Hello world")
  private val actorSystem: ActorSystem = ActorSystem("TowerGame")
  private val configProps = GameHost.props(GameConfig.Default)
  private val gameHost: ActorRef = actorSystem.actorOf(configProps, GameHost.ActorName)
  private val server: ApiServer = new ApiServer(actorSystem)
  server.start()
  while (true) {
    Thread.sleep(sleepTime)
    logCurrentWinner(gameHost)
  }

  def requestCurrentWinner() : Option[Color] = {
    requestCurrentWinner(gameHost)
  }
  private def requestCurrentWinner(gameHost: ActorRef) : Option[Color] = {
    val timeout = Timeout(1 minute)
    val query: WinningColorQuery = WinningColorQuery(timeout)
    requestCurrentWinner(gameHost, query)
  }

  private def requestCurrentWinner(gameHost: ActorRef, query: WinningColorQuery): Option[Color] = {
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
    val resultOption = requestCurrentWinner(gameHost)
    if (resultOption.isDefined) {
      val result = resultOption.get
      logger.info(s"${result.ansiCode}[logCurrentWinner] Got winning color as: $result${Color.RESET.ansiCode}")
    } else {
      logger.info("[logCurrentWinner] No winning color defined!")
    }
  }
}

object GameConfig {
  val ZeroValues: GameConfig = GameConfig(0, 0, 0, 0)
  val Default: GameConfig = GameConfig(5, 50, 5, 5)
}
case class GameConfig(coordinatorCount: Int,
                      buildersPerCoordinator: Int,
                      towerSpaceCount: Int,
                      towersPerSpace: Int) {
}