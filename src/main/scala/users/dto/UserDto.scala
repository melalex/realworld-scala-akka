package com.melalex.realworld
package users.dto

case class UserDto(
    email: String,
    token: String,
    username: String,
    bio: Option[String],
    image: Option[String]
)
