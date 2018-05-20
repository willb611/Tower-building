package com.github.willb611.api

import com.github.willb611.{Color, TowerGame}
import com.typesafe.scalalogging.LazyLogging

class GameService
  extends LazyLogging {

  def getWinningColor: String = {
    val winner = TowerGame.requestCurrentWinner().getOrElse(Color.RED)
    val resultString = winner.description.toString
    logger.debug(s"[getWinningColor] Returning $resultString")
    resultString
  }
}
