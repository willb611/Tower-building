//package com.github.willb611
//
//import com.github.willb611.humans.Builder
//
//class GameClock() {
//  var builders: List[Builder] = List()
//  var towers: List[ProxyTower] = List()
//  var environment: Environment = Environment.Default
//
//  def withBuilder(builder: Builder): Unit = {
//    if (builder != null) {
//      builders = builder :: builders
//    }
//  }
//
//  def withTower(tower: ProxyTower): Unit = {
//    if (tower != null) {
//      towers = tower :: towers
//    }
//  }
//
//  def withEnvironment(env: Environment): Unit = {
//    if (env != null) {
//      environment = env
//    }
//  }
//
//  def runForTime(time: Int): Unit = {
//    for (builder <- builders) {
//      builder.doWork(time)
//      environment.applyAnyEffects(builder)
//    }
//    for (tower <- towers) {
//      environment.applyAnyEffects(tower)
//      tower.processPendingBlocks()
//    }
//  }
//}