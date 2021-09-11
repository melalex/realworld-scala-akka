package com.melalex.realworld
package profiles

import commons.db.DBIOInstances
import profiles.service.ProfileService
import profiles.service.impl.ProfileServiceImpl
import users.repository.UserRepository
import users.route.UserRouteProvider

import cats.instances.FutureInstances
import com.softwaremill.macwire.wire
import slick.dbio.DBIO

import scala.concurrent.{ExecutionContext, Future}

trait ProfileComponents { self: FutureInstances with DBIOInstances =>

  def userRepository: UserRepository[DBIO]

  implicit def executor: ExecutionContext

  lazy val profileService: ProfileService[Future] = wire[ProfileServiceImpl[Future, DBIO]]
  lazy val userRouteProvider: UserRouteProvider   = wire[UserRouteProvider]
}
