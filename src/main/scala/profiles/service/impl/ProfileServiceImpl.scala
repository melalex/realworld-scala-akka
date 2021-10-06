package com.melalex.realworld
package profiles.service.impl

import commons.auth.model.{ActualUserPrincipal, UserPrincipal}
import commons.db.DbInterpreter
import commons.errors.model.{NotFoundException, RealWorldError}
import profiles.model.Profile
import profiles.service.ProfileService
import users.model.Username
import users.repository.UserRepository

import cats.Monad
import cats.data._

class ProfileServiceImpl[F[_]: Monad, DB[_]: Monad](
    userRepository: UserRepository[DB],
    dbInterpreter: DbInterpreter[F, DB]
) extends ProfileService[F] {

  override def follow(
      followingUsername: Username
  )(implicit user: ActualUserPrincipal): F[Either[NotFoundException, Profile]] = {
    val unitOfWork = for {
      follower  <- getUserByName(user.username)
      following <- getUserByName(followingUsername)
      _         <- EitherT.right[NotFoundException](userRepository.createRelation(follower.id, following.id))
    } yield Profile(following.id, following.username, following.bio, following.image, following = true)

    dbInterpreter.executeTransitionally(unitOfWork.value)
  }

  override def unfollow(
      followingUsername: Username
  )(implicit user: ActualUserPrincipal): F[Either[NotFoundException, Profile]] = {
    val unitOfWork = for {
      follower  <- getUserByName(user.username)
      following <- getUserByName(followingUsername)
      _         <- EitherT.right[NotFoundException](userRepository.deleteRelation(follower.id, following.id))
    } yield Profile(following.id, following.username, following.bio, following.image, following = false)

    dbInterpreter.executeTransitionally(unitOfWork.value)
  }

  override def getByUsername(
      followingUsername: Username
  )(implicit user: UserPrincipal): F[Either[NotFoundException, Profile]] = {
    val unitOfWork = OptionT(userRepository.findByUsername(followingUsername, user.id))
      .toRight(RealWorldError.NotFound(followingUsername.value).ex[NotFoundException])

    dbInterpreter.executeTransitionally(unitOfWork.value)
  }

  private def getUserByName(followingUsername: Username) = OptionT(userRepository.findByUsername(followingUsername))
    .toRight(RealWorldError.NotFound(followingUsername.value).ex[NotFoundException])
}
