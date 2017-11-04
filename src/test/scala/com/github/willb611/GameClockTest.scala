package com.github.willb611

import org.scalatest.FunSuite

class GameClockTest extends FunSuite {
  test("Worker timer makes workers do work") {
    val tower = new Tower
    val worker = new Worker(tower)
    val gameClock = new GameClock
    gameClock.withWorker(worker)
    gameClock.runForTime(5)
    assert(tower.height >= 1)
  }
}
