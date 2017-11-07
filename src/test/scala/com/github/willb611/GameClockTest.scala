package com.github.willb611

import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite

class GameClockTest extends FunSuite with MockFactory {
  test("Timer makes builders do work") {
    val tower = new Tower
    val builder = new Builder(tower)
    val gameClock = new GameClock
    gameClock.withBuilder(builder)
    gameClock.runForTime(5)
    assert(tower.height >= 1)
  }

  test("Timer uses environment to apply effects") {
    val tower = new Tower
    val builder = new Builder(tower)
    val gameClock = new GameClock
    gameClock.withBuilder(builder)
    gameClock.withTower(tower)
    var envMock = mock[Environment]
    gameClock.withEnvironment(envMock)
    // expects
    (envMock.applyAnyEffects(_: Builder)).expects(builder)
    (envMock.applyAnyEffects(_: Tower)).expects(tower)
    // run
    gameClock.runForTime(1)
  }

  test("Only 1 builder can place blocks at a time") {
    val tower = new Tower
    val b1 = new Builder(tower)
    val b2 = new Builder(tower)
    val gameClock = new GameClock
    gameClock.withBuilder(b1)
    gameClock.withBuilder(b2)
    gameClock.runForTime(1)
    assert(tower.height == 1)
  }
}
