package com.github.willb611

import com.typesafe.scalalogging.LazyLogging

import scala.collection.immutable.HashMap

object TowerGame extends LazyLogging {
  def main(args: Array[String]): Unit = {
    logger.debug("Hello Tower")
    val game = new TowerGame
    game.setup()
    game.run()
  }
}

class TowerGame(var builders: List[Builder], var towers: List[ProxyTower]) extends LazyLogging {
  val TWO_SECONDS: Int = 2000
  val WORKERS_PER_TOWER: Int = 5
  val TOWERS_TO_MAKE: Int = 5
  val clock: GameClock = new GameClock

  def this() = this(List(), List())
  
  {
    updateClockFromBuildersAndTowers()
  }


  def updateClockFromBuildersAndTowers(): Unit = {
    if (towers != null && towers.nonEmpty) {
      towers.foreach(clock.withTower)
    }
    if (builders != null && builders.nonEmpty) {
      builders.foreach(clock.withBuilder)
    }
  }

  def setup(): Unit = {
    for (_ <- 0 until TOWERS_TO_MAKE) {
      val tower = new ProxyTower
      towers = tower :: towers
      for (_ <- 0 until WORKERS_PER_TOWER) {
        val builder = new Builder(tower)
        builders = builder :: builders
      }
    }
    updateClockFromBuildersAndTowers()
  }

  def run(): Unit = {
    logState()
    for (_ <- 0 until 2) {
      logger.info("Sleeping..")
      Thread.sleep(TWO_SECONDS)
      clock.runForTime(1)
      logState()
    }
  }

  def currentlyWinningColor(): Option[Color] = {
    if (towers.nonEmpty) {
      val colorCountMap: Map[Color, Int] = buildCountOfTowerColors()
      ColorCollectionHelper.highestPercentColorFromMap(colorCountMap)
    } else {
      None
    }
  }

  private def buildCountOfTowerColors(): Map[Color, Int] = {
    var colorCountMap: Map[Color, Int] = HashMap()
    for (tower <- towers) {
      val colorOption = tower.highestPercentColor()
      if (colorOption.nonEmpty) {
        val color = colorOption.get
        if (colorCountMap.contains(color)) {
          colorCountMap += (color -> (colorCountMap.getOrElse(color, 0) + 1))
        } else {
          colorCountMap = colorCountMap + (color -> 1)
        }
      }
    }
    colorCountMap
  }

  def logState(): Unit = {
    logger.info("Workers: " + builders.length)
    logger.info("Towers list: " + towers.length)
    for (t <- towers) {
      logger.info(t.consoleOutputString())
    }
    logger.info(Console.RESET)
  }
}
