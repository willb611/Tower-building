package com.github.willb611

import scala.util.Random
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite
import com.github.willb611.EnvironmentEffects.EnvironmentEffect

class EnvironmentTest extends FunSuite with MockFactory {
  ignore("Apply any effect should apply an effect if random is true") {
    val randomMock = mock[Random]
    var tower = stub[Tower]
    var env = new Environment(randomMock)

    // randomMock expects nextBoolean returning true
    (randomMock.nextBoolean _).expects().returns(true)

    // run
    env.applyAnyEffects(tower)

    // verify
    (tower.environmentEffect _).verify(EnvironmentEffects.Lightning)
  }
}