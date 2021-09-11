package com.melalex.realworld
package profiles.model

case class Profile(
    username: String,
    bio: Option[String],
    image: Option[String],
    following: Boolean
)
