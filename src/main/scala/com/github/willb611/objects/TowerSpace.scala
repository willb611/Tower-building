package com.github.willb611.objects

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.github.willb611.ColorCollectionHelper.CountOfColors
import com.github.willb611.messages.Query
import com.github.willb611.objects.Environment.ActorJoinEnvironmentAdvisory
import com.github.willb611.objects.TowerSpace.{CountOfTowersWithColorRequest, TowersInSpaceQuery}

import scala.collection.mutable.ListBuffer

object TowerSpace {
  def props(environment: ActorRef, towersPerSpace: Int): Props = Props(new TowerSpace(environment, towersPerSpace))

  case class CountOfTowersWithColorRequest()
  case object TowersInSpaceQuery extends Query
}

class TowerSpace(environment: ActorRef, towersToMake: Int) extends Actor with ActorLogging {
  private val towers = ListBuffer[ActorRef]()

  override def preStart(): Unit = {
    for (_ <- 0 until towersToMake) {
      val t = context.actorOf(Props[Tower])
      environment ! ActorJoinEnvironmentAdvisory(t)
      towers += t
    }
  }

  override def receive = {
    case CountOfTowersWithColorRequest =>
      log.error("[receive] TODO")
      sender() ! CountOfColors.EmptyCount
    case TowersInSpaceQuery =>
      sender() ! towers.toList
    case advisory: ActorJoinEnvironmentAdvisory =>
      environment forward advisory
  }
}
