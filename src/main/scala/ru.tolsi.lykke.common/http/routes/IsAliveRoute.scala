package ru.tolsi.lykke.common.http.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import play.api.libs.json._

object IsAliveRoute {

  case class ResponseObject(name: String, version: String, env: String, isDebug: Boolean)

  implicit val ResponseWrites: Writes[ResponseObject] = Json.writes[ResponseObject]
}

/**
  * Is alive api method route
  *
  * @param name    Name of the service
  * @param version Version of the service
  * @param env     ENV_INFO environment variable value
  * @param isDebug Flag, which indicates if the service is built in the debug configuration or not
  */
case class IsAliveRoute(name: String,
                        version: String,
                        env: String,
                        isDebug: Boolean) extends PlayJsonSupport {

  import IsAliveRoute._

  private val responseObject = Json.toJson(ResponseObject(name, version, env, isDebug))

  val route: Route = path("isalive") {
    get {
      complete(responseObject)
    }
  }
}
