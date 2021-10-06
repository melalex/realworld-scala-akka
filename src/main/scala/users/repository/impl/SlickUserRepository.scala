package com.melalex.realworld
package users.repository.impl

import commons.db.{DBIOInstances, SlickInstances}
import commons.model.{Email, ModelId}
import profiles.model.Profile
import users.model._
import users.repository.UserRepository

import cats.data.OptionT
import slick.dbio.DBIO
import slick.jdbc.MySQLProfile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}

import scala.concurrent.ExecutionContext

class SlickUserRepository(implicit executionContext: ExecutionContext)
    extends UserRepository[DBIO]
    with DBIOInstances
    with SlickInstances {

  override def save(user: User): DBIO[SavedUser] = user match {
    case it: SavedUser   => users.update(it).map(_ => it)
    case it: UnsavedUser => (users.returning(users.map(_.id)) += it.asSaved(ModelId.Unsaved)).map(id => it.asSaved(id))
  }

  override def createRelation(followerId: ModelId, followingId: ModelId): DBIO[UserToUserRelation] = {
    val toAdd = UserToUserRelation(followerId, followingId)

    userToUser
      .insertOrUpdate(toAdd)
      .map(_ => toAdd)
  }

  override def deleteRelation(followerId: ModelId, followingId: ModelId): DBIO[Unit] = userToUser
    .filter(it => it.followerId === followerId && it.followingId === followingId)
    .delete
    .map(_ => ())

  override def findByEmail(email: Email): DBIO[Option[SavedUser]] = users
    .filter(_.email === email)
    .take(1)
    .result
    .headOption

  override def findByUsername(username: Username): DBIO[Option[SavedUser]] = users
    .filter(_.username === username)
    .take(1)
    .result
    .headOption

  override def findByUsername(username: Username, callerId: ModelId): DBIO[Option[Profile]] = {
    def findByUsernameInternal(): DBIO[Option[(SavedUser, Option[UserToUserRelation])]] =
      users
        .joinLeft(userToUser)
        .on((u, r) => u.id === r.followingId && r.followerId === callerId)
        .filter { case (user, _) => user.username === username }
        .result
        .headOption

    OptionT(findByUsernameInternal()).map { case (user, maybeRelation) =>
      Profile(user.id, user.username, user.bio, user.image, maybeRelation.isDefined)
    }.value
  }
}
