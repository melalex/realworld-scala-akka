package com.melalex.realworld
package comments

import articles.repository.ArticleRepository
import comments.mapper.CommentMapper
import comments.repository.impl.SlickCommentRepository
import comments.route.CommentRouteProvider
import comments.service.CommentFactory
import comments.service.impl.CommentServiceImpl
import commons.auth.service.TokenService
import commons.db.{DBIOInstances, DbInterpreter}
import profiles.mapper.ProfileMapper

import cats.instances.FutureInstances
import com.softwaremill.macwire.wire
import slick.dbio.DBIO

import java.time.{Clock, ZoneId}
import scala.concurrent.{ExecutionContext, Future}

trait CommentComponents { self: FutureInstances with DBIOInstances =>

  def tokenService: TokenService
  def clock: Clock
  def zoneId: ZoneId
  def profileMapper: ProfileMapper
  def articleRepository: ArticleRepository[DBIO]
  def dbInterpreter: DbInterpreter[Future, DBIO]

  implicit def executor: ExecutionContext

  lazy val commentRouteProvider: CommentRouteProvider = wire[CommentRouteProvider]

  private[comments] lazy val commentMapper     = wire[CommentMapper]
  private[comments] lazy val commentRepository = wire[SlickCommentRepository]
  private[comments] lazy val commentFactory    = wire[CommentFactory]
  private[comments] lazy val commentService    = wire[CommentServiceImpl[Future, DBIO]]
}
