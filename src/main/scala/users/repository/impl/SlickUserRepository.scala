package com.melalex.realworld
package users.repository.impl

import commons.model.ModelId
import users.model.{PasswordHash, SavedUser, UnSavedUser, User}
import users.repository.UserRepository
import users.repository.impl.SlickUserRepository.query

import slick.dbio.DBIO
import slick.jdbc.MySQLProfile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}
import slick.lifted.{ProvenShape, _}

import java.time.Instant
import scala.concurrent.ExecutionContext

class SlickUserRepository(
    implicit executionContext: ExecutionContext
) extends UserRepository[DBIO] {

  override def save(user: User): DBIO[SavedUser] = user match {
    case it: SavedUser   => query.update(it).andThen(DBIO.successful(it))
    case it: UnSavedUser => (query.returning(query.map(_.id)) += it.asSaved(ModelId.UnSaved)).map(id => it.asSaved(id))
  }

  override def findByEmail(email: String): DBIO[Option[SavedUser]] =
    query
      .filter(_.email === email)
      .take(1)
      .result
      .headOption

  override def findById(id: ModelId): DBIO[Option[SavedUser]] =
    query
      .filter(_.id === id)
      .take(1)
      .result
      .headOption
}

object SlickUserRepository {

  val query = TableQuery[UserSchema]

  class UserSchema(tag: Tag) extends Table[SavedUser](tag, "user") {

    def id: Rep[ModelId]            = column[ModelId]("id", O.PrimaryKey, O.AutoInc)
    def email: Rep[String]          = column[String]("email", O.Unique, O.Length(320))
    def username: Rep[String]       = column[String]("username", O.Unique, O.Length(40))
    def password: Rep[PasswordHash] = column[PasswordHash]("password")
    def bio: Rep[String]            = column[String]("bio", O.Length(254))
    def image: Rep[String]          = column[String]("image")
    def createdAt: Rep[Instant]     = column[Instant]("created_at")
    def updatedAt: Rep[Instant]     = column[Instant]("updated_at")

    override def * : ProvenShape[SavedUser] =
      (id, email, username, password, bio.?, image.?, createdAt, updatedAt) <>
        ((SavedUser.apply _).tupled, SavedUser.unapply)
  }
}
