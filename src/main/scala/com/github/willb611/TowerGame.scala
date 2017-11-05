package com.github.willb611

object TowerGame {
  def main(args: Array[String]): Unit = {
    println("Hello Tower")
    var game = new TowerGame
    game.setup()
    game.run()
  }
}

class TowerGame {
  val TWO_SECONDS: Int = 2000;
  val WORKERS_PER_TOWER: Int = 5
  val TOWERS_TO_MAKE: Int = 5
  var workers: List[Worker] = List()
  var towers: List[Tower] = List()
  val clock: GameClock = new GameClock

  def setup(): Unit = {
    for (towersCreated <- 0 until TOWERS_TO_MAKE) {
      var tower = new Tower
      towers = towers ++ List(tower)
      for (workersCreated <- 0 until WORKERS_PER_TOWER) {
        var worker = new Worker(tower)
        workers = workers ++ List(worker)
        clock.withWorker(worker)
      }
      clock.withTower(tower)
    }
  }

  def run(): Unit = {
    printState()
    for (cnt <- 0 until 5) {
      println("[TowerGame] Sleeping..")
      Thread.sleep(TWO_SECONDS)
      clock.runForTime(1)
      printState()
    }
  }

  def printState(): Unit = {
    println("Workers: " + workers.length)
    println("Towers list: " + towers.length)
    for (t <- towers) {
      println(t)
    }
  }
}
