package com.melalex.realworld
package users.dto

case class UserDto(
    user: UserDto.Body
)

object UserDto {

  case class Body(
      email: String,
      token: String,
      username: String,
      bio: Option[String],
      image: Option[String]
  )
}
