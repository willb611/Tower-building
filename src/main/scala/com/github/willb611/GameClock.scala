package com.github.willb611

class GameClock() {
  var builders: List[Builder] = List()
  var towers: List[ProxyTower] = List()
  var environment: Environment = Environment.Default

  def withBuilder(builder: Builder): Unit = {
    builders = builder :: builders
  }

  def withTower(tower: ProxyTower): Unit = {
    towers = tower :: towers
  }

  def withEnvironment(env: Environment): Unit = {
    environment = env
  }

  def runForTime(time: Int): Unit = {
    for (builder <- builders) {
      builder.doWork(time)
      environment.applyAnyEffects(builder)
    }
    for (tower <- towers) {
      environment.applyAnyEffects(tower)
      tower.processPendingBlocks()
    }
  }
}