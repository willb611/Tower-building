package com.github.willb611.messages

import akka.actor.{Actor, ActorLogging}

trait UnhandledMessagesLogged
  extends Actor
    with ActorLogging {
  override def unhandled(message: Any): Unit = {
    log.warning(s"[unhandled] From sender: ${sender()}, unexpected message: $message")
    super.unhandled(message)
  }
}
