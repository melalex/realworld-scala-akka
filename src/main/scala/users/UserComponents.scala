package com.melalex.realworld
package users

import commons.auth.service.TokenService
import commons.db.{DBIOInstances, DbInterpreter}
import commons.util.InstantProvider
import commons.web.RouteProvider
import users.repository.impl.SlickUserRepository
import users.route.UserRouteProvider
import users.service.UserFactory
import users.service.impl.{BCryptPasswordHashService, UserServiceImpl}

import cats.instances.FutureInstances
import com.softwaremill.macwire.wire
import slick.dbio.DBIO

import scala.concurrent.{ExecutionContext, Future}

trait UserComponents { self: FutureInstances with DBIOInstances =>

  def instantProvider: InstantProvider
  def tokenService: TokenService
  def dbInterpreter: DbInterpreter[Future, DBIO]

  implicit def executor: ExecutionContext

  lazy val userRouteProvider: RouteProvider           = wire[UserRouteProvider]
  lazy val userService: UserServiceImpl[Future, DBIO] = wire[UserServiceImpl[Future, DBIO]]
  lazy val userRepository: SlickUserRepository        = wire[SlickUserRepository]

  private[users] lazy val passwordHash = wire[BCryptPasswordHashService]
  private[users] lazy val userFactory  = wire[UserFactory]
}
