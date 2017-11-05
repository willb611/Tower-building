package com.github.willb611

object TowerGame {
  def main(args: Array[String]): Unit = {
    println("Hello Tower")
    val game = new TowerGame
    game.setup()
    game.run()
  }
}

class TowerGame {
  val TWO_SECONDS: Int = 2000
  val WORKERS_PER_TOWER: Int = 5
  val TOWERS_TO_MAKE: Int = 5
  var builders: List[Builder] = List()
  var towers: List[Tower] = List()
  val clock: GameClock = new GameClock

  def setup(): Unit = {
    for (_ <- 0 until TOWERS_TO_MAKE) {
      val tower = new Tower
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
    printState()
    for (_ <- 0 until 2) {
      println("[TowerGame] Sleeping..")
      Thread.sleep(TWO_SECONDS)
      clock.runForTime(1)
      printState()
    }
  }

  def printState(): Unit = {
    println("Workers: " + builders.length)
    println("Towers list: " + towers.length)
    for (t <- towers) {
      println(t.consoleOutputString())
    }
    print(Console.RESET)
  }
}
