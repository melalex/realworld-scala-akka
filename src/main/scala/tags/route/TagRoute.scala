package com.melalex.realworld
package tags.route

import commons.web.RouteProvider

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

class TagRoute extends RouteProvider {

  override def provideRoute: Route = path("tags") {
    complete(StatusCodes.OK)
  }
}
