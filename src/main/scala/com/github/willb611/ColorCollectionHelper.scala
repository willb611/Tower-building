package com.github.willb611

import scala.collection.immutable.{HashMap, SortedSet}

object ColorCollectionHelper {
  def highestPercentColorFromMap(colorCountMap: Map[Color, Int]): Option[Color] = {
    if (colorCountMap.nonEmpty) {
      val highestCounts = swapColorCountMap(colorCountMap)
      var sortedByCount: SortedSet[Int] = SortedSet()
      sortedByCount = sortedByCount ++ highestCounts.keySet

      val key = sortedByCount.lastKey
      highestCounts.get(key)
    } else {
      return None
    }
  }

  private def swapColorCountMap(colorCountMap: Map[Color, Int]): Map[Int, Color] = {
    HashMap() ++ colorCountMap.map(_.swap)
  }
}
