package com.github.willb611

import com.typesafe.scalalogging.LazyLogging

object TowerGame extends LazyLogging {
  def main(args: Array[String]): Unit = {
    logger.debug("Hello Tower")
    val game = new TowerGame
    game.setup()
    game.run()
  }
}

class TowerGame extends LazyLogging {
  val TWO_SECONDS: Int = 2000
  val WORKERS_PER_TOWER: Int = 5
  val TOWERS_TO_MAKE: Int = 5
  var builders: List[Builder] = List()
  var towers: List[Tower] = List()
  val clock: GameClock = new GameClock

  def setup(): Unit = {
    for (_ <- 0 until TOWERS_TO_MAKE) {
      val tower = new ProxyTower
      towers = tower :: towers
      for (_ <- 0 until WORKERS_PER_TOWER) {
        val builder = new Builder(tower)
        builders = builder :: builders
        clock.withBuilder(builder)
      }
      clock.withTower(tower)
    }
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

  def logState(): Unit = {
    logger.info("Workers: " + builders.length)
    logger.info("Towers list: " + towers.length)
    for (t <- towers) {
      logger.info(t.consoleOutputString())
    }
    logger.info(Console.RESET)
  }
}
