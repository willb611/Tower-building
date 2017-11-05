package com.github.willb611

import scala.util.Random
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite

class EnvironmentTest extends FunSuite with MockFactory {
  ignore("Apply any effect should apply an effect if random is true") {
    val randomMock = mock[Random]
    val tower = stub[Tower]
    val worker = stub[Worker]
    val env = new Environment(randomMock)

    (randomMock.nextBoolean _).expects().returns(true)

    // run
    env.applyAnyEffects(tower)
    env.applyAnyEffects(worker)

    // verify
    (tower.environmentEffect _).verify(EnvironmentEffects.Lightning)
    (worker.environmentEffect _).verify(EnvironmentEffects.Lightning)
  }

  test("Apply any effect should apply an effect to a worker if random is true") {
    val randomMock = mock[Random]
    val worker = stub[Worker]
    var env = new Environment(randomMock)

    (randomMock.nextBoolean _).expects().returns(true)

    // run
    env.applyAnyEffects(worker)

    // verify
    (worker.environmentEffect _).verify(EnvironmentEffects.Lightning)
  }
}