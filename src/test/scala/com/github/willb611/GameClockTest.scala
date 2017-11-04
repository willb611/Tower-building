package com.github.willb611

import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite

class GameClockTest extends FunSuite with MockFactory {
  test("Timer makes workers do work") {
    val tower = new Tower
    val worker = new Worker(tower)
    val gameClock = new GameClock
    gameClock.withWorker(worker)
    gameClock.runForTime(5)
    assert(tower.height >= 1)
  }

  test("Timer applies random effects occasionaly") {
    val tower = new Tower
    val worker = new Worker(tower)
    val gameClock = new GameClock
    gameClock.withWorker(worker)
    gameClock.withTower(tower)
    var envMock = mock[Environment]
    gameClock.withEnvironment(envMock)
    // expects
    (envMock.applyAnyEffects(_: Worker)).expects(worker)
    (envMock.applyAnyEffects(_: Tower)).expects(tower)

    // run
    gameClock.runForTime(1)

    // // verify
    // (envMock.applyAnyEffects(_)).verify(worker)
    // (envMock.applyAnyEffects(_: Worker)).verify(worker)
    // envMock.verify(worker)
  }
}
