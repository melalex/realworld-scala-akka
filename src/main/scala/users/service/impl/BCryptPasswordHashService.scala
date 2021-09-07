package com.melalex.realworld
package users.service.impl

import users.model.PasswordHash
import users.service.PasswordHashService

import org.mindrot.jbcrypt.BCrypt

class BCryptPasswordHashService extends PasswordHashService {

  override def hash(plainTextPassword: String): PasswordHash = PasswordHash(BCrypt.hashpw(plainTextPassword, BCrypt.gensalt()))

  override def verify(plainTextPassword: String, actualHash: PasswordHash): Boolean = BCrypt.checkpw(plainTextPassword, actualHash.value)
}
