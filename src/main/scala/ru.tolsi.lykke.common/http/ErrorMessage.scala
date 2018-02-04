package ru.tolsi.lykke.common.http

import play.api.libs.json.{Json, Writes}

object ErrorMessage {
  implicit val errorMessageWrites: Writes[ErrorMessage] = Json.writes[ErrorMessage]
}
case class ErrorMessage(errorMessage: String, modelErrors: Option[Map[String, Seq[String]]] = None)