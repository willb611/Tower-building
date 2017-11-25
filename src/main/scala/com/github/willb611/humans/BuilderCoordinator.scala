package com.github.willb611.humans

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.github.willb611.Color

import scala.collection.mutable.ListBuffer

object BuilderCoordinator {
  def props(buildersToCreate: Int, color: Color): Props = Props(new BuilderCoordinator(buildersToCreate, color))
}

class BuilderCoordinator(buildersToCreate: Int, color: Color) extends Actor with ActorLogging {
  var builders: ListBuffer[ActorRef] = ListBuffer()
  var towers: ListBuffer[ActorRef] = ListBuffer()

  override def preStart(): Unit = {
    for (_ <- 0 until buildersToCreate) {
      val builder = context.actorOf(Builder.props(color))
      builders += builder
    }
    super.preStart()
  }

  override def receive = {
    case msg => unhandled(msg)
  }
}
