package com.melalex.realworld
package users.dto

case class UserAuthenticationDto(
    user: UserAuthenticationDto.Body
)

object UserAuthenticationDto {

  case class Body(
      email: String,
      password: String
  )
}
