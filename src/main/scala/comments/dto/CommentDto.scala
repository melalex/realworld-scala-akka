package com.melalex.realworld
package comments.dto

import profiles.dto.ProfileDto

import java.time.ZonedDateTime

case class CommentDto(
    id: Long,
    createdAt: ZonedDateTime,
    updatedAt: ZonedDateTime,
    body: String,
    author: ProfileDto
)
