package com.github.willb611.objects

import akka.actor.{Actor, ActorLogging}
import com.github.willb611.ColorCollectionHelper.CountOfColors
import com.github.willb611.messages.{Command, Query}
import com.github.willb611.objects.Environment.ApplyEffectCommand
import com.github.willb611.objects.EnvironmentEffects.EnvironmentEffect
import com.github.willb611.objects.Tower._
import com.github.willb611.{Color, ColorCollectionHelper}

object Tower {
  // Messages
  final case class AddBlockRequest(colorToUseForBlocks: Color)
  final case object ProcessPendingBlocks extends Command

  final case object CountCountQuery extends Query
  final case object HeightQuery extends Query
  final case object LastColorQuery extends Query
}

class Tower() extends Actor with ActorLogging {
  private var blocks: List[Color] = List()
  private var pendingBlocks: List[Color] = List()

  override def receive = {
    case message: AddBlockRequest =>
      pendingBlocks = message.colorToUseForBlocks :: pendingBlocks
    // commands
    case message: ApplyEffectCommand =>
      environmentEffect(message.environmentEffect)
    case ProcessPendingBlocks =>
      processPendingBlocks()
    // queries
    case CountCountQuery =>
      sender() ! colorCount()
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
    val colorToPlace = ColorCollectionHelper.colorCountFromList(pendingBlocks).highestPercentColor()
    if (colorToPlace.nonEmpty) {
      pendingBlocks = List()
      blocks = colorToPlace.get :: blocks
    }
    log.debug(s"[processPendingBlocks] Finished, height now ${height()}")
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
    val prefix = s"[Tower]{h=${height()}}"
    if (highestPercentColor().isDefined) {
      var result = prefix + s",colored=${highestPercentColor().get},blocks={"
      result += blocks mkString ","
      result += "}"
      result
    } else {
      prefix
    }
  }

  def consoleOutputString(): String = { 
    highestPercentColor().getOrElse(Color.RESET).ansiCode + toString() + Color.RESET.ansiCode
  }
}
