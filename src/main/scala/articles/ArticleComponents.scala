package com.melalex.realworld
package articles

import articles.mapper.ArticleMapper
import articles.repository.impl.SlickArticleRepository
import articles.route.ArticleRouteProvider
import articles.service.ArticleFactory
import articles.service.impl.ArticleServiceImpl
import commons.auth.service.TokenService
import commons.db.{DBIOInstances, DbInterpreter}
import profiles.mapper.ProfileMapper

import cats.instances.FutureInstances
import com.softwaremill.macwire.wire
import slick.dbio.DBIO

import java.time.{Clock, ZoneId}
import scala.concurrent.{ExecutionContext, Future}

trait ArticleComponents { self: FutureInstances with DBIOInstances =>

  def tokenService: TokenService
  def clock: Clock
  def zoneId: ZoneId
  def profileMapper: ProfileMapper
  def dbInterpreter: DbInterpreter[Future, DBIO]

  implicit def executor: ExecutionContext

  lazy val articleRouteProvider: ArticleRouteProvider       = wire[ArticleRouteProvider]
  lazy val articleRepository: SlickArticleRepository        = wire[SlickArticleRepository]
  lazy val articleService: ArticleServiceImpl[Future, DBIO] = wire[ArticleServiceImpl[Future, DBIO]]

  private[articles] lazy val articleFactory = wire[ArticleFactory]
  private[articles] lazy val articleMapper  = wire[ArticleMapper]
}
