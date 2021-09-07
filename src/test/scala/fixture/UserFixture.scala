package com.melalex.realworld
package fixture

import fixture.UserFixture._
import users.model.{PasswordHash, UnSavedUser}
import users.service.impl.BCryptPasswordHashService

trait UserFixture extends TimeFixture {

  val unSaved: UnSavedUser = UnSavedUser(
    email = Email,
    username = Username,
    password = Password,
    bio = Bio,
    image = Image,
    createdAt = Now,
    updatedAt = Now
  )
}

object UserFixture {

  val Email: String             = "test@test.com"
  val Username: String          = "Username"
  val PlainTextPassword: String = "Password"
  val Password: PasswordHash    = new BCryptPasswordHashService().hash(PlainTextPassword)
  val Bio: Option[String]       = Some("Bio")
  val Image: Option[String]     = Some("Image")
}
