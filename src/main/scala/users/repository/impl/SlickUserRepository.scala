package com.melalex.realworld
package users.repository.impl

import commons.db.{DbInitRequired, Droppable}
import commons.model.ModelId
import users.model.{PasswordHash, SavedUser, UnsavedUser, User}
import users.repository.UserRepository
import users.repository.impl.SlickUserRepository.UserQuery

import slick.dbio.DBIO
import slick.jdbc.MySQLProfile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}
import slick.lifted.{ProvenShape, _}

import java.time.Instant
import scala.concurrent.ExecutionContext

class SlickUserRepository(
    implicit executionContext: ExecutionContext
) extends UserRepository[DBIO]
    with DbInitRequired[DBIO]
    with Droppable[DBIO] {

  override def init(): DBIO[Unit] = UserQuery.schema.createIfNotExists

  override def drop(): DBIO[Unit] = UserQuery.schema.dropIfExists

  override def save(user: User): DBIO[SavedUser] = user match {
    case it: SavedUser   => UserQuery.update(it).andThen(DBIO.successful(it))
    case it: UnsavedUser => (UserQuery.returning(UserQuery.map(_.id)) += it.asSaved(ModelId.UnSaved)).map(id => it.asSaved(id))
  }

  override def findByEmail(email: String): DBIO[Option[SavedUser]] =
    UserQuery
      .filter(_.email === email)
      .take(1)
      .result
      .headOption

  override def findById(id: ModelId): DBIO[Option[SavedUser]] =
    UserQuery
      .filter(_.id === id)
      .take(1)
      .result
      .headOption
}

object SlickUserRepository {

  val UserQuery = TableQuery[UserSchema]

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
}
