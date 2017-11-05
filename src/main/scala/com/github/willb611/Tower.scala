package com.github.willb611

import com.github.willb611.EnvironmentEffects.EnvironmentEffect
import scala.collection.immutable.HashMap
import scala.collection.immutable.TreeMap
import scala.collection.immutable.SortedSet

object Tower {
  val DEFAULT_COLOR: Color = new Color("FFFFFF")

  private var instancesMadeSoFar: Int = 0;
  def nextId(): Int = {
    var cpy = instancesMadeSoFar
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
    if (height > 0) {
      var colorCountMap: Map[Color, Int] = buildColorCountMap()
      highestPercentColorFromMap(colorCountMap)
    } else {
      None
    }
  }

  private def buildColorCountMap(): Map[Color, Int] = {
    var colorCountMap: Map[Color, Int] = HashMap()
    for (coloredBlock <- blocks) {
      if (colorCountMap.contains(coloredBlock)) {
        colorCountMap = colorCountMap + (coloredBlock
           -> (colorCountMap.getOrElse(coloredBlock, 0) + 1))
      } else {
        colorCountMap = colorCountMap + (coloredBlock -> 1)
      }
    }
    colorCountMap
  }

  private def swapColorCountMap(colorCountMap: Map[Color, Int]): Map[Int, Color] = {
    (HashMap() ++ colorCountMap.map(_.swap))
  }

  private def highestPercentColorFromMap(colorCountMap: Map[Color, Int]): Option[Color] = {
    var highestCounts = swapColorCountMap(colorCountMap)
    var sortedByCount: SortedSet[Int] = SortedSet()
    sortedByCount = (sortedByCount ++ highestCounts.keySet)

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

  def environmentEffect(effect: EnvironmentEffect) = {
    blocks = blocks.drop(1)
  }

  override def toString(): String = {
    val prefix = "[Tower] id=" + id + ",h=" + height
    if (highestPercentColor != None) {
      prefix + ",colored=" + highestPercentColor.get
    } else {
      prefix
    }
  }

  def consoleOutputString(): String = { 
    highestPercentColor.getOrElse(Color.RESET).ansiCode + toString
  }
}
