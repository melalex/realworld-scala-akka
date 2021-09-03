package com.melalex.realworld
package commons.web

import akka.http.scaladsl.server.Route

trait RouteProvider {

  def provideRoute: Route
}
