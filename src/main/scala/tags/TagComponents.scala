package com.melalex.realworld
package tags

import articles.service.ArticleService
import tags.mapper.TagMapper
import tags.route.TagRouteProvider

import com.softwaremill.macwire.wire

import scala.concurrent.{ExecutionContext, Future}

trait TagComponents {

  def articleService: ArticleService[Future]

  implicit def executor: ExecutionContext

  lazy val tagRouteProvider: TagRouteProvider = wire[TagRouteProvider]

  private[tags] lazy val tagMapper = wire[TagMapper]
}
