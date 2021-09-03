package com.melalex.realworld
package users.repository

import commons.model.ModelId
import users.model.{SavedUser, User}

trait UserRepository[DB[_]] {

  def save(user: User): DB[SavedUser]

  def findByEmail(email: String): DB[Option[SavedUser]]

  def findById(id: ModelId): DB[Option[SavedUser]]
}
