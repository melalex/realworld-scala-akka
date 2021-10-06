package com.melalex.realworld
package config

import articles.ArticleComponents
import comments.CommentComponents
import commons.CommonComponents
import commons.auth.AuthComponents
import commons.db.{DBIOInstances, DbComponents}
import commons.errors.ErrorComponents
import commons.i18n.I18nComponents
import commons.web.RouteProvider
import commons.web.impl.CompositeRouteProvider
import profiles.ProfileComponents
import tags.TagComponents
import users.UserComponents

import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Route
import cats.instances.FutureInstances
import com.softwaremill.macwire.{wire, wireSet}

trait AppComponents
    extends PureConfigComponents
    with UserComponents
    with ProfileComponents
    with ArticleComponents
    with TagComponents
    with CommentComponents
    with I18nComponents
    with ErrorComponents
    with DbComponents
    with AuthComponents
    with CommonComponents
    with FutureInstances
    with DBIOInstances {

  lazy val routes: Route = pathPrefix("api") {
    compositeRouteProvider.provideRoute
  }

  lazy val realWorldProperties: RealWorldProperties = configSource
    .at("realworld")
    .loadOrThrow[RealWorldProperties]

  private[config] lazy val routeProviders         = wireSet[RouteProvider]
  private[config] lazy val compositeRouteProvider = wire[CompositeRouteProvider]
}
