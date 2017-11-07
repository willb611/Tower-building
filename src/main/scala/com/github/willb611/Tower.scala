package com.github.willb611

import com.github.willb611.EnvironmentEffects.EnvironmentEffect
import scala.collection.immutable.HashMap
import scala.collection.immutable.SortedSet

object Tower {
  val DEFAULT_COLOR: Color = new Color("FFFFFF")

  private var instancesMadeSoFar: Int = 0
  def nextId(): Int = {
    val cpy = instancesMadeSoFar
    instancesMadeSoFar += 1
    cpy
  }
}

class Tower() {
  private val id = Tower.nextId()
  private var blocks: List[Color] = List()

  def height(): Int = blocks.length

  def lastColor(): Option[Color] = {
    if (height > 0) {
      Some(blocks.head)
    } else {
      None
    }
  }

  def highestPercentColor(): Option[Color] = {
    highestPercentColorFromBlockList(blocks)
  }
  protected def highestPercentColorFromBlockList(colorBlocks: List[Color]): Option[Color] = {
    if (colorBlocks.nonEmpty) {
      val colorCountMap: Map[Color, Int] = buildColorCountMap(colorBlocks)
      highestPercentColorFromMap(colorCountMap)
    } else {
      None
    }
  }

  private def buildColorCountMap(colorBlocks: List[Color]): Map[Color, Int] = {
    var colorCountMap: Map[Color, Int] = HashMap()
    for (coloredBlock <- colorBlocks) {
      if (colorCountMap.contains(coloredBlock)) {
        colorCountMap += (coloredBlock -> (colorCountMap.getOrElse(coloredBlock, 0) + 1))
      } else {
        colorCountMap = colorCountMap + (coloredBlock -> 1)
      }
    }
    colorCountMap
  }

  private def swapColorCountMap(colorCountMap: Map[Color, Int]): Map[Int, Color] = {
    HashMap() ++ colorCountMap.map(_.swap)
  }

  private def highestPercentColorFromMap(colorCountMap: Map[Color, Int]): Option[Color] = {
    val highestCounts = swapColorCountMap(colorCountMap)
    var sortedByCount: SortedSet[Int] = SortedSet()
    sortedByCount = sortedByCount ++ highestCounts.keySet

    val key = sortedByCount.lastKey
    highestCounts.get(key)
  }

  def addBlock(str: Color): Tower = {
    blocks = str :: blocks
    this
  }
  def addBlock(): Tower = {
    addBlock(Tower.DEFAULT_COLOR)
  }

  def environmentEffect(effect: EnvironmentEffect): Unit = {
    blocks = blocks.drop(1)
  }

  override def toString: String = {
    val prefix = "[Tower] id=" + id + ",h=" + height
    if (highestPercentColor().isDefined) {
      var result = prefix + ",colored=" + highestPercentColor().get + ",blocks={"
      result += blocks mkString ","
      result += "}"
      result
    } else {
      prefix
    }
  }

  def consoleOutputString(): String = { 
    highestPercentColor().getOrElse(Color.RESET).ansiCode + toString()
  }
}
