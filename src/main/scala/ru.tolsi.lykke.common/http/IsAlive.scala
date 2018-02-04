package ru.tolsi.lykke.common.http

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import play.api.libs.json.{JsBoolean, JsObject, JsString}

trait IsAlive extends PlayJsonSupport {
  // Name of the service
  def name: String

  // Version of the service
  def version: String

  // ENV_INFO environment variable value
  def env: String

  // Flag, which indicates if the service is built
  // in the debug configuration or not
  def isDebug: Boolean

  private val responseObject =
    JsObject(Map(
      "name" -> JsString(name),
      "version" -> JsString(version),
      "env" -> JsString(env),
      "isDebug" -> JsBoolean(isDebug)))

  val isAliveRoute: Route = path("isalive") {
    get {
      complete(responseObject)
    }
  }
}
