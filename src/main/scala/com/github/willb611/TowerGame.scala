package com.github.willb611

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await
import scala.concurrent.duration._


object TowerGame extends App with LazyLogging {
  val twoSecondsInMillis: Int = 2000
  val oneMinuteTimeout = Timeout(1 minute)

  override def main(args: Array[String]): Unit = {
    logger.debug("Hello Tower")
    val actorSystem: ActorSystem = ActorSystem("TowerGame")
    val coordinator = actorSystem.actorOf(Props(new GameHost(GameConfig())), "gameHost")
    Thread.sleep(twoSecondsInMillis)
    val optionalWinningColor = ask(coordinator, GameHost.WinningColorRequest(Timeout(5 seconds)))(oneMinuteTimeout).mapTo[Option[Color]]
    val result: Option[Color] = Await.result(optionalWinningColor, oneMinuteTimeout.duration)
    if (result.isDefined) {
      logger.info("[main] Got winning color as: {}", result)
    } else {
      logger.info("[main] No winning color defined!")
    }
    actorSystem.terminate()
  }
}


case class GameConfig() {
  val coordinators: Int = 5
  val buildersPerCoordinator = 15
  val spacesForTowers = 1
  val towersPerSpace = 5
}