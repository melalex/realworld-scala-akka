package com.melalex.realworld
package profiles.service

import commons.auth.model.{ActualUserPrincipal, UserPrincipal}
import commons.errors.model.NotFoundException
import profiles.model.Profile
import users.model.Username

trait ProfileService[F[_]] {

  def follow(followingUsername: Username)(implicit user: ActualUserPrincipal): F[Either[NotFoundException, Profile]]

  def unfollow(followingUsername: Username)(implicit user: ActualUserPrincipal): F[Either[NotFoundException, Profile]]

  def getByUsername(followingUsername: Username)(implicit user: UserPrincipal): F[Either[NotFoundException, Profile]]
}
