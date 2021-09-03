package com.melalex.realworld
package users.model

case class UpdateUser(
    email: String,
    bio: Option[String],
    image: Option[String]
)
