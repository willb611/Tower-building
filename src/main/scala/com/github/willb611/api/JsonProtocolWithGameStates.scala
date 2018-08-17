package com.github.willb611.api

import com.github.willb611.ColorCollectionHelper.CountOfColors
import com.github.willb611.objects.{TowerSpaceState, TowerState}
import com.github.willb611.{Color, GameState}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat}

object JsonProtocolWithGameStates
  extends DefaultJsonProtocol {
  implicit object colorFormat extends JsonFormat[Color] {
    def write(c: Color) = JsString(c.description)

    def read(value: JsValue) = value match {
      case JsString(name) =>
        new Color(name, "???")
      case _ => throw new IllegalArgumentException("Color expected")
    }
  }

  implicit val countOfColorsFormat: JsonFormat[CountOfColors] = jsonFormat1(CountOfColors.apply)
  implicit val towerStateFormat: JsonFormat[TowerState] = jsonFormat2(TowerState)
  implicit val towerSpaceStateFormat: JsonFormat[TowerSpaceState] = jsonFormat2(TowerSpaceState)
  implicit val gameStateFormat: JsonFormat[GameState] = jsonFormat1(GameState)
}
