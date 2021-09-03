package com.melalex.realworld
package users.dto

case class UserUpdateDto(
    user: UserUpdateDto.Body
)

object UserUpdateDto {

  case class Body(
      email: String,
      bio: Option[String],
      image: Option[String]
  )
}
