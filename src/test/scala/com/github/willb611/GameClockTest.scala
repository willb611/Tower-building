//package com.github.willb611
//
//import com.github.willb611.humans.Builder
//import com.github.willb611.objects.Tower
//import org.scalamock.scalatest.MockFactory
//import org.scalatest.FunSuite
//
//class GameClockTest extends FunSuite with MockFactory {
//  test("Timer makes builders do work") {
//    val tower = new Tower
//    val builder = new Builder(tower)
//    val gameClock = new GameClock
//    gameClock.withBuilder(builder)
//    gameClock.runForTime(5)
//    assert(tower.height >= 1)
//  }
//
//  test("Only 1 builder can place blocks at a time") {
//    val tower = new ProxyTower
//    val b1 = new Builder(tower)
//    val b2 = new Builder(tower)
//    val gameClock = new GameClock
//    gameClock.withBuilder(b1)
//    gameClock.withBuilder(b2)
//    gameClock.withTower(tower)
//    gameClock.runForTime(1)
//    assert(tower.height == 1)
//  }
//}
