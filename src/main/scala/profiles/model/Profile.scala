package com.melalex.realworld
package profiles.model

import commons.model.ModelId
import users.model.Username

case class Profile(
    id: ModelId,
    username: Username,
    bio: Option[String],
    image: Option[String],
    following: Boolean
)
