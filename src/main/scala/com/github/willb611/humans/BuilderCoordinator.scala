package com.github.willb611.humans

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.github.willb611.humans.BuilderCoordinator.TowerListRequest
import com.github.willb611.objects.{Environment, Tower}

import scala.collection.mutable.ListBuffer

object BuilderCoordinator {
  abstract trait Message
  final case class TowerListRequest() extends Message
}

class BuilderCoordinator(buildersToCreate: Int) extends Actor with ActorLogging {
  var builders: ListBuffer[ActorRef] = ListBuffer()
  var towers: ListBuffer[ActorRef] = ListBuffer()

  override def preStart(): Unit = {
    for (_ <- 0 until buildersToCreate) {
      val builder = context.actorOf(Props[Builder])
      builders += builder
    }
    super.preStart()
  }

  override def receive = {
    case environment: Environment =>
      log.info("[receive] From {} got {}", sender(), environment)
      sender() ! TowerListRequest()
    case tower: Tower =>
      towers += sender()
  }
}
