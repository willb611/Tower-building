package com.github.willb611

import scala.collection.immutable.{HashMap, SortedSet}

object ColorCollectionHelper {
  object CountOfColors {
    val EmptyCount = CountOfColors(HashMap())
  }
  case class CountOfColors(map: Map[Color, Int]) {
    def sum(): Int = {
      var result = 0
      for (key <- map.keys) {
        result = result + map(key)
      }
      result
    }

    def updateWith(countToBeIncluded: CountOfColors): CountOfColors = {
      val mapToAdd = countToBeIncluded.map
      var result: Map[Color, Int] = map
      for (key <- mapToAdd .keys) {
        result = result + (key -> (mapToAdd(key) + result.getOrElse(key, 0)))
      }
      CountOfColors(result)
    }

    def highestPercentColor(): Option[Color] = {
      if (map.nonEmpty) {
        val highestCounts = swapColorCountMap()
        var sortedByCount: SortedSet[Int] = SortedSet()
        sortedByCount = sortedByCount ++ highestCounts.keySet

        val key = sortedByCount.lastKey
        highestCounts.get(key)
      } else {
        None
      }
    }
    private def swapColorCountMap(): Map[Int, Color] = {
      HashMap() ++ map.map(_.swap)
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

  def colorCountFromList(colorBlocks: List[Color]): CountOfColors = {
    if (colorBlocks.nonEmpty) {
      CountOfColors(buildColorCountMap(colorBlocks))
    } else {
      CountOfColors.EmptyCount
    }
  }
}
