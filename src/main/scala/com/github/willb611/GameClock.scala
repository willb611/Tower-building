package com.github.willb611

import com.github.willb611.EnvironmentEffects.EnvironmentEffect

class GameClock() {
  var workers: List[Worker] = List()

  def withWorker(worker: Worker): Unit = {
    workers = List(worker) ++ workers
  }

  def runForTime(time: Int): Unit = {
    for (worker <- workers) {
      worker.doWork(time)
    }
  }
}
