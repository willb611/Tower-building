package com.github.willb611.objects

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import com.github.willb611.ColorCollectionHelper.CountOfColors
import com.github.willb611.messages.{Command, Query}
import com.github.willb611.objects.Environment.{ActorJoinEnvironmentAdvisory, ApplyEffectCommand}
import com.github.willb611.objects.EnvironmentEffects.EnvironmentEffect
import com.github.willb611.objects.Tower._
import com.github.willb611.ColorCollectionHelper
import com.github.willb611.Color

import scala.concurrent.duration.{FiniteDuration, _}

object Tower {
  def props(parent: ActorRef): Props = Props(new Tower(Some(parent)))
  val ActorNamePrefix: String = "tower"
  val processBlocksInterval: FiniteDuration = 100 millis
  private final case object TimerKey
  // Messages
  final case class AddBlockRequest(colorToUseForBlocks: Color)
  final case object ProcessPendingBlocksCommand extends Command

  final case object CountCountQuery extends Query
  final case object HeightQuery extends Query
  final case object LastColorQuery extends Query
}

class Tower(parent: Option[ActorRef])
  extends Actor
    with ActorLogging
    with Timers {
  def this() = this(None)
  private var blocks: List[Color] = List()
  private var pendingBlocks: List[Color] = List()

  override def preStart(): Unit = {
    timers.startPeriodicTimer(TimerKey, ProcessPendingBlocksCommand, processBlocksInterval)
    super.preStart()
  }

  private def parentActor(): ActorRef = {
    if (parent.isDefined) {
      parent.get
    } else {
      context.parent
    }
  }

  override def receive = {
    case message: AddBlockRequest =>
      parentActor() ! ActorJoinEnvironmentAdvisory(sender())
      pendingBlocks = message.colorToUseForBlocks :: pendingBlocks
    // commands
    case message: ApplyEffectCommand =>
      environmentEffect(message.environmentEffect)
    case ProcessPendingBlocksCommand =>
      processPendingBlocks()
    // queries
    case CountCountQuery =>
      sender() ! colorCount()
      log.info(s"[receive] Received $CountCountQuery, current state: $toString")
    case HeightQuery =>
      sender() ! height()
    case LastColorQuery =>
      sender() ! lastColor()
  }

  override def unhandled(message: Any): Unit = {
    log.debug("[unhandled] Given unexpected message: {}", message)
    super.unhandled(message)
  }

  def processPendingBlocks(): Unit = {
    val startHeight = height()
    val colorToPlace = ColorCollectionHelper.colorCountFromList(pendingBlocks).highestPercentColor()
    if (colorToPlace.nonEmpty) {
      pendingBlocks = List()
      blocks = colorToPlace.get :: blocks
    }
    if (startHeight != height()) {
      log.debug(s"[processPendingBlocks] Processed blocks, state is now: $toString")
    } else {
      log.debug(s"[processPendingBlocks] Finished, no change")
    }
  }

  def height(): Int = blocks.length

  def lastColor(): Option[Color] = {
    if (height > 0) {
      Some(blocks.head)
    } else {
      None
    }
  }

  private def highestPercentColor(): Option[Color] = {
    colorCount().highestPercentColor()
  }

  private def colorCount(): CountOfColors = {
    ColorCollectionHelper.colorCountFromList(blocks)
  }

  def addBlock(str: Color): Tower = {
    pendingBlocks = str :: pendingBlocks
    this
  }

  private def environmentEffect(effect: EnvironmentEffect): Unit = {
    blocks = blocks.drop(1)
    log.debug(s"[environmentEffect] Effected by $effect, height now ${height()}")
  }

  override def toString: String = {
    val prefix = s"[Tower]{h=${height()}"
    if (highestPercentColor().isDefined) {
      var result = prefix + s",colored=${highestPercentColor().get},blocks={"
      result += blocks mkString ","
      result += "}"
      result
    } else {
      prefix + ",blocks={}}"
    }
  }

  def consoleOutputString(): String = { 
    highestPercentColor().getOrElse(Color.RESET).ansiCode + toString() + Color.RESET.ansiCode
  }
}
