package com.github.willb611

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.github.willb611.GameHost.WinningColorQuery
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object TowerGame extends App with LazyLogging {
  override def main(args: Array[String]): Unit = {
    logger.debug("[main] Hello world")
    val actorSystem: ActorSystem = ActorSystem("TowerGame")
    val coordinator: ActorRef = actorSystem.actorOf(Props(new GameHost(GameConfig())), "gameHost")
    Thread.sleep(4000)
    logCurrentWinner(coordinator)
    actorSystem.terminate()
  }

  private def logCurrentWinner(coordinator: ActorRef): Unit = {
    val timeout = Timeout(1 minute)
    val query: WinningColorQuery = WinningColorQuery(Timeout(5 seconds))
    logger.info("[main] Using coordinator: {}, timeout: {}, query: {}", coordinator, timeout, query)
    val coordinatorResponse: Future[Any] = coordinator.ask(query)(timeout)
    val result: Any = Await.result(coordinatorResponse, timeout.duration)
    val resultAsColorOption: Option[Color] = result.asInstanceOf[Option[Color]]
    if (resultAsColorOption.isDefined) {
      logger.info("[main] Got winning color as: {}", result)
    } else {
      logger.info("[main] No winning color defined!")
    }
  }
}

case class GameConfig() {
  val coordinators: Int = 5
  val buildersPerCoordinator: Int = 15
  val spacesForTowers: Int = 1
  val towersPerSpace: Int = 5
}