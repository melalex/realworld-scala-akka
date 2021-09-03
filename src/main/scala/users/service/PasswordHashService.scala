package com.melalex.realworld
package users.service

import users.model.{PasswordHash, User}

trait PasswordHashService {

  def hash(plainTextPassword: String): PasswordHash

  def verify(plainTextPassword: String, actualHash: PasswordHash): Boolean
}

object PasswordHashService {

  implicit class PasswordHashServiceOps(val value: PasswordHashService) extends AnyVal {

    def verify(plainTextPassword: String)(actualUser: User): Boolean = value.verify(plainTextPassword, actualUser.password)
  }
}
