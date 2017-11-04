package com.github.willb611

class GameClock() {
  var workers: List[Worker] = List()
  var towers: List[Tower] = List()
  var environment = Environment.Default;

  def withWorker(worker: Worker): Unit = {
    workers = List(worker) ++ workers
  }

  def withTower(tower: Tower): Unit = {
    towers = List(tower) ++ towers
  }

  def withEnvironment(env: Environment): Unit = {
    environment = env;
  }

  def runForTime(time: Int): Unit = {
    for (worker <- workers) {
      worker.doWork(time)
      environment.applyAnyEffects(worker)
    }
    for (tower <- towers) {
      environment.applyAnyEffects(tower)
    }
  }
}
