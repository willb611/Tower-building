package com.github.willb611.objects

import akka.actor.{Actor, ActorLogging, ActorRef, Props, SupervisorStrategy}
import akka.pattern.ask
import akka.util.Timeout
import com.github.willb611.ColorCollectionHelper.{CountOfColors, countOfColorsFromOneColor}
import com.github.willb611.builders.BuilderCoordinator.TowerListAdvisory
import com.github.willb611.{Color, RestartKilledSupervisionStrategy, UnhandledMessagesLogged}
import com.github.willb611.messages.Query
import com.github.willb611.objects.Environment.ActorJoinEnvironmentAdvisory
import com.github.willb611.objects.Tower.CountCountQuery
import com.github.willb611.objects.TowerSpace.{CountOfTowersWithColorQuery, TowersInSpaceQuery}

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object TowerSpace {
  val ActorNamePrefix: String = "towerSpace"

  def props(environment: ActorRef, towersPerSpace: Int): Props = Props(new TowerSpace(environment, towersPerSpace))
  // Messages
  final case object CountOfTowersWithColorQuery extends Query
  final case object TowersInSpaceQuery extends Query
}

class TowerSpace(environment: ActorRef, towersToMake: Int)
  extends Actor
    with ActorLogging
    with UnhandledMessagesLogged {
  override val supervisorStrategy: SupervisorStrategy = RestartKilledSupervisionStrategy(super.supervisorStrategy).strategy
  private val towers = ListBuffer[ActorRef]()
  private val timeoutWhenQueryingTowers: Timeout = Timeout(10 seconds)
  private val towerNameIterator = Iterator from 1 map (i => s"${Tower.ActorNamePrefix}-$i")

  override def preStart(): Unit = {
    for (_ <- 0 until towersToMake) {
      val t = context.actorOf(Props[Tower], towerNameIterator.next())
      environment ! ActorJoinEnvironmentAdvisory(t)
      towers += t
    }
  }

  def getCountOfColorsFromTowers: CountOfColors = {
    var result: CountOfColors = CountOfColors.EmptyCount
    towers.foreach(tower => {
      val colorOption = queryTowerForCurrentColorMajority(tower)
      if (colorOption.isDefined) {
        result = result.updateWith(countOfColorsFromOneColor(colorOption.get))
      }
    })
    result
  }

  private def queryTowerForCurrentColorMajority(tower: ActorRef): Option[Color] = {
    try {
      val query = CountCountQuery
      val gameHostResponse: Future[Any] = tower.ask(query)(timeoutWhenQueryingTowers)
      val resultAsTypeAny: Any = Await.result(gameHostResponse, timeoutWhenQueryingTowers.duration)
      val colorsFromTower = resultAsTypeAny.asInstanceOf[CountOfColors]
      val towerColor = colorsFromTower.highestPercentColor()
      towerColor
    } catch {
      case e: Exception =>
        log.warning(s"[getCountOfColorsFromTowers] Exception querying tower $tower: ${e.getMessage}", e)
        None
    }
  }

  override def receive = {
    case advisory: ActorJoinEnvironmentAdvisory =>
      environment forward advisory
    // queries
    case CountOfTowersWithColorQuery =>
      val combinedCount: CountOfColors = getCountOfColorsFromTowers
      log.info(s"[receive] Responding to $CountOfTowersWithColorQuery with $combinedCount")
      sender() ! combinedCount
    case TowersInSpaceQuery =>
      sender() ! TowerListAdvisory(towers.toList)
  }
}
