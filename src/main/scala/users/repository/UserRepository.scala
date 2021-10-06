package com.melalex.realworld
package users.repository

import commons.model.{Email, ModelId}
import profiles.model.Profile
import users.model.{SavedUser, User, UserToUserRelation, Username}

trait UserRepository[DB[_]] {

  def save(user: User): DB[SavedUser]

  def createRelation(followerId: ModelId, followingId: ModelId): DB[UserToUserRelation]

  def deleteRelation(followerId: ModelId, followingId: ModelId): DB[Unit]

  def findByEmail(email: Email): DB[Option[SavedUser]]

  def findByUsername(username: Username): DB[Option[SavedUser]]

  def findByUsername(username: Username, callerId: ModelId): DB[Option[Profile]]
}
