package com.melalex.realworld
package users.repository

import commons.model.ModelId
import profiles.model.Profile
import users.model.{SavedUser, User, UserToUserRelation}

trait UserRepository[DB[_]] {

  def save(user: User): DB[SavedUser]

  def createRelation(followerId: ModelId, followingId: ModelId): DB[UserToUserRelation]

  def deleteRelation(followerId: ModelId, followingId: ModelId): DB[Unit]

  def findByEmail(email: String): DB[Option[SavedUser]]

  def findByUsername(username: String): DB[Option[SavedUser]]

  def findByUsername(username: String, callerId: ModelId): DB[Option[Profile]]

  def findById(id: ModelId): DB[Option[SavedUser]]

  def findById(id: ModelId, callerId: ModelId): DB[Option[Profile]]
}
