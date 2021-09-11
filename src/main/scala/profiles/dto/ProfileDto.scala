package com.melalex.realworld
package profiles.dto

case class ProfileDto(
    profile: ProfileDto.Body
)

object ProfileDto {

  case class Body(
      username: String,
      bio: Option[String],
      image: Option[String],
      following: Boolean
  )
}
