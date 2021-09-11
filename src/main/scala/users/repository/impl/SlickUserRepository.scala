package com.melalex.realworld
package users.repository.impl

import commons.db.{DBIOInstances, DbInitRequired, Droppable}
import commons.model.ModelId
import profiles.model.Profile
import users.model._
import users.repository.UserRepository
import users.repository.impl.SlickUserRepository.{UserToUserRelations, Users}

import cats.data.OptionT
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

  override def init(): DBIO[Unit] = Users.schema.createIfNotExists

  override def drop(): DBIO[Unit] = Users.schema.dropIfExists

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

  override def findByUsername(username: String, followerId: ModelId): DBIO[Option[Profile]] = {
    val join = for {
      (user, relation) <- Users joinLeft UserToUserRelations on ((u, r) => u.id === r.followingId && r.followerId === followerId)
      if user.username === username
    } yield (user, relation)

    val result: DBIO[Option[(SavedUser, Option[UserToUserRelation])]] = join.result.headOption

    OptionT(result).map {
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

object SlickUserRepository {

  val Users               = TableQuery[UserSchema]
  val UserToUserRelations = TableQuery[UserToUserRelationSchema]

  class UserSchema(tag: Tag) extends Table[SavedUser](tag, "user") {

    def id        = column[ModelId]("id", O.PrimaryKey, O.AutoInc)
    def email     = column[String]("email", O.Unique, O.Length(320))
    def username  = column[String]("username", O.Unique, O.Length(40))
    def password  = column[PasswordHash]("password", O.Length(240))
    def bio       = column[Option[String]]("bio", O.Length(254))
    def image     = column[Option[String]]("image", O.Length(320))
    def createdAt = column[Instant]("created_at")
    def updatedAt = column[Instant]("updated_at")

    override def * : ProvenShape[SavedUser] =
      (id, email, username, password, bio, image, createdAt, updatedAt) <>
        ((SavedUser.apply _).tupled, SavedUser.unapply)
  }

  class UserToUserRelationSchema(tag: Tag) extends Table[UserToUserRelation](tag, "user_to_user") {

    def followerId  = column[ModelId]("follower_id")
    def followingId = column[ModelId]("following_id")
    def pk          = primaryKey("pk_user_to_user", (followerId, followingId))
    def follower    = foreignKey("fk_follower", followerId, Users)(_.id, onDelete = ForeignKeyAction.Cascade)
    def following   = foreignKey("fk_following", followingId, Users)(_.id, onDelete = ForeignKeyAction.Cascade)

    override def * = (followerId, followingId) <> ((UserToUserRelation.apply _).tupled, UserToUserRelation.unapply)
  }
}
