package com.github.willb611.objects

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.github.willb611.objects.Environment.InEnvironment
import com.github.willb611.objects.TowerSpace.CountOfTowersWithColorRequest

import scala.collection.mutable.ListBuffer

object TowerSpace {
  case class CountOfTowersWithColorRequest()
}

class TowerSpace(environment: ActorRef, towersToMake: Int) extends Actor with ActorLogging {
  private val towers = ListBuffer[ActorRef]()

  override def preStart(): Unit = {
    for (_ <- 0 until towersToMake) {
      val t = context.actorOf(Props[Tower])
      environment ! InEnvironment(t)
      towers += t
    }
  }

  override def receive = {
    case CountOfTowersWithColorRequest =>
      throw new Exception("TODO")
//      sender() ! ColorCollectionHelper.CountOfColors(null)
  }
}
