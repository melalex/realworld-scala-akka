package com.melalex.realworld
package profiles.service.impl

import commons.db.DbInterpreter
import commons.errors.model.{NotFoundException, RealWorldError}
import commons.model.ModelId
import profiles.model.Profile
import profiles.service.ProfileService
import users.repository.UserRepository

import cats.Monad
import cats.data._

class ProfileServiceImpl[F[_]: Monad, DB[_]: Monad](
    userRepository: UserRepository[DB],
    dbInterpreter: DbInterpreter[F, DB]
) extends ProfileService[F] {

  override def follow(followerId: ModelId, followingUsername: String): F[Either[NotFoundException, Profile]] = {
    val unitOfWork = for {
      _         <- getUserById(followerId)
      following <- getUserByName(followingUsername)
      _         <- EitherT.right(userRepository.createRelation(followerId, following.id))
    } yield Profile(following.username, following.bio, following.image, following = true)

    dbInterpreter.executeTransitionally(unitOfWork.value)
  }

  override def unfollow(followerId: ModelId, followingUsername: String): F[Either[NotFoundException, Profile]] = {
    val unitOfWork = for {
      _         <- getUserById(followerId)
      following <- getUserByName(followingUsername)
      _         <- EitherT.right(userRepository.deleteRelation(followerId, following.id))
    } yield Profile(following.username, following.bio, following.image, following = false)

    dbInterpreter.executeTransitionally(unitOfWork.value)
  }

  override def getByUsername(followerId: ModelId, followingUsername: String): F[Either[NotFoundException, Profile]] = {
    val unitOfWork = OptionT(userRepository.findByUsername(followingUsername, followerId))
      .toRight(RealWorldError.NotFound(followingUsername).ex[NotFoundException])

    dbInterpreter.executeTransitionally(unitOfWork.value)
  }

  private def getUserByName(followingUsername: String) =
    OptionT(userRepository.findByUsername(followingUsername))
      .toRight(RealWorldError.NotFound(followingUsername).ex[NotFoundException])

  private def getUserById(followerId: ModelId) =
    OptionT(userRepository.findById(followerId))
      .toRight(RealWorldError.NotFound(followerId).ex[NotFoundException])
}
