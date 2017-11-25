//package com.github.willb611
//
//import com.github.willb611.humans.Builder
//import com.github.willb611.objects.Tower
//
//import scala.util.Random
//import org.scalamock.scalatest.MockFactory
//import org.scalatest.FunSuite
//
//class EnvironmentTest extends FunSuite with MockFactory {
//  test("Apply any effect should apply an effect if random is true") {
//    val randomMock = mock[Random]
//    val tower = stub[Tower]
//    val env = new Environment(randomMock)
//    // expect
//    (randomMock.nextBoolean _).expects().returns(true)
//    // run
//    env.applyAnyEffects(tower)
//    // verify
//    (tower.environmentEffect _).verify(EnvironmentEffects.Lightning)
//  }
//
//  test("Apply any effect should apply an effect to a builder if random is true") {
//    val randomMock = mock[Random]
//    val builder = stub[Builder]
//    val env = new Environment(randomMock)
//    // expect
//    (randomMock.nextBoolean _).expects().returns(true)
//    // run
//    env.applyAnyEffects(builder)
//    // verify
//    (builder.environmentEffect _).verify(EnvironmentEffects.Lightning)
//  }
//}