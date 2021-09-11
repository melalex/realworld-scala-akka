package com.melalex.realworld
package profiles.service

import commons.errors.model.NotFoundException
import commons.model.ModelId
import profiles.model.Profile

trait ProfileService[F[_]] {

  def follow(followerId: ModelId, followingUsername: String): F[Either[NotFoundException, Profile]]

  def unfollow(followerId: ModelId, followingUsername: String): F[Either[NotFoundException, Profile]]

  def getByUsername(followerId: ModelId, followingUsername: String): F[Either[NotFoundException, Profile]]
}
