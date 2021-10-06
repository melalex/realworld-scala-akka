package com.melalex.realworld
package tags.route

import articles.service.ArticleService
import commons.web.RouteProvider
import tags.mapper.TagMapper

import akka.http.scaladsl.server.Route
import io.circe.generic.auto._

import scala.concurrent.{ExecutionContext, Future}

class TagRouteProvider(
    articleService: ArticleService[Future],
    tagMapper: TagMapper
)(implicit ec: ExecutionContext)
    extends RouteProvider {

  override def provideRoute: Route = path("tags") {
    complete(articleService.getAllTags.map(tagMapper.map))
  }
}
