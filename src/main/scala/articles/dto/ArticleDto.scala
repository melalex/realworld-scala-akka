package com.melalex.realworld
package articles.dto

import profiles.dto.ProfileDto

import java.time.ZonedDateTime

case class ArticleDto(
    slug: String,
    tittle: String,
    description: String,
    body: String,
    tagList: List[String],
    createdAt: ZonedDateTime,
    updatedAt: ZonedDateTime,
    favorite: Boolean,
    favoritesCount: Int,
    author: ProfileDto
)
