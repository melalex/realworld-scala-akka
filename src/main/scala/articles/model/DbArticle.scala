package com.melalex.realworld
package articles.model

import commons.model.ModelId

import java.time.Instant

case class DbArticle(
    id: ModelId,
    slug: Slug,
    tittle: String,
    description: String,
    body: String,
    authorId: ModelId,
    updatedAt: Instant,
    createdAt: Instant
)
