package ru.tolsi.lykke.common.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, RejectionHandler, Route, ValidationRejection}
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import play.api.libs.json.Json

import scala.util.control.NonFatal

trait LykkeApiServer extends Directives with PlayJsonSupport {
  private val jsonRejectionHandler = RejectionHandler.newBuilder().handle {
    case ValidationRejection(error, Some(NonFatal(t))) => complete(StatusCodes.BadRequest -> Json.toJson(ErrorMessage(error)))
  }.result()

  def handleRejections(r: Route): Route = handleRejections(jsonRejectionHandler) {
    r
  }
}
