package com.melalex.realworld
package profiles

import commons.auth.service.TokenService
import commons.db.{DBIOInstances, DbInterpreter}
import profiles.mapper.ProfileMapper
import profiles.route.ProfileRouteProvider
import profiles.service.impl.ProfileServiceImpl
import users.repository.UserRepository

import cats.instances.FutureInstances
import com.softwaremill.macwire.wire
import slick.dbio.DBIO

import scala.concurrent.{ExecutionContext, Future}

trait ProfileComponents { self: FutureInstances with DBIOInstances =>

  def userRepository: UserRepository[DBIO]
  def dbInterpreter: DbInterpreter[Future, DBIO]
  def tokenService: TokenService

  implicit def executor: ExecutionContext

  lazy val profileRouteProvider: ProfileRouteProvider = wire[ProfileRouteProvider]
  lazy val profileMapper: ProfileMapper               = wire[ProfileMapper]

  private[profiles] lazy val profileService = wire[ProfileServiceImpl[Future, DBIO]]
}
