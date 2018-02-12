package ru.tolsi.lykke.common

import play.api.libs.json.{JsError, JsSuccess, JsValue, Reads}

import scala.util.Try

object NetworkType {

  def fromString(s: String): NetworkType = s match {
    case Test.name => Test
    case Main.name => Main
  }

  case object Test extends NetworkType {
    override val name: String = "test"
  }

  case object Main extends NetworkType {
    override val name: String = "main"
  }

  implicit val networkTypeReader: Reads[NetworkType] =
    (json: JsValue) =>
      Try(NetworkType.fromString(json.as[String]))
        .map(JsSuccess(_))
        .getOrElse(JsError(s"Network type should be a string '${Test.name}' or '${Main.name}'"))
}

sealed trait NetworkType {
  def name: String
}
