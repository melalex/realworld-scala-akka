package com.melalex.realworld
package users.repository.impl

import commons.db.{DBIOInstances, DbInitRequired, Droppable}
import commons.model.ModelId
import profiles.model.Profile
import users.model._
import users.repository.UserRepository
import users.repository.impl.SlickUserRepository.{UserToUserRelations, Users}

import cats.data.OptionT
import com.melalex.realworld.commons.db.SlickSchemas.{UserToUserRelations, Users}
import slick.dbio.DBIO
import slick.jdbc.MySQLProfile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}
import slick.lifted.{ProvenShape, _}

import java.time.Instant
import scala.concurrent.ExecutionContext

class SlickUserRepository(
    implicit executionContext: ExecutionContext
) extends UserRepository[DBIO]
    with DbInitRequired[DBIO]
    with Droppable[DBIO]
    with DBIOInstances {

  override def init(): DBIO[Unit] =
    DBIO
      .sequence(
        Seq(
          Users.schema.createIfNotExists,
          UserToUserRelations.schema.createIfNotExists
        )
      )
      .map(_ => ())

  override def drop(): DBIO[Unit] =
    DBIO
      .sequence(
        Seq(
          Users.schema.dropIfExists,
          UserToUserRelations.schema.dropIfExists
        )
      )
      .map(_ => ())

  override def save(user: User): DBIO[SavedUser] = user match {
    case it: SavedUser   => Users.update(it).map(_ => it)
    case it: UnsavedUser => (Users.returning(Users.map(_.id)) += it.asSaved(ModelId.UnSaved)).map(id => it.asSaved(id))
  }

  override def createRelation(followerId: ModelId, followingId: ModelId): DBIO[UserToUserRelation] = {
    val toAdd = UserToUserRelation(followerId, followingId)

    // Migrate INSERT -> UPSERT
    (UserToUserRelations += toAdd).map(_ => toAdd)
  }

  override def deleteRelation(followerId: ModelId, followingId: ModelId): DBIO[Unit] =
    UserToUserRelations
      .filter(it => it.followerId === followerId && it.followingId === followingId)
      .delete
      .map(_ => ())

  override def findByEmail(email: String): DBIO[Option[SavedUser]] =
    Users
      .filter(_.email === email)
      .take(1)
      .result
      .headOption

  override def findByUsername(username: String): DBIO[Option[SavedUser]] =
    Users
      .filter(_.username === username)
      .take(1)
      .result
      .headOption

  override def findByUsername(username: String, callerId: ModelId): DBIO[Option[Profile]] = {
    def findByUsernameInternal(): DBIO[Option[(SavedUser, Option[UserToUserRelation])]] = {
      val join = for {
        (user, relation) <- Users joinLeft UserToUserRelations on ((u, r) => u.id === r.followingId && r.followerId === callerId)
        if user.username === username
      } yield (user, relation)

      join.result.headOption
    }

    OptionT(findByUsernameInternal()).map {
      case (user, maybeRelation) => Profile(user.username, user.bio, user.image, maybeRelation.isDefined)
    }.value
  }

  override def findById(id: ModelId): DBIO[Option[SavedUser]] =
    Users
      .filter(_.id === id)
      .take(1)
      .result
      .headOption
}
