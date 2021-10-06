package com.melalex.realworld
package articles.model

import commons.model.ModelId
import profiles.model.Profile
import tags.model.ArticleTag

import java.time.Instant

trait Article {

  val slug: Slug
  val tittle: String
  val description: String
  val body: String
  val tags: Set[ArticleTag]
  val authorId: ModelId
  val createdAt: Instant
  val updatedAt: Instant
}

case class SavedArticle(
    id: ModelId,
    slug: Slug,
    tittle: String,
    description: String,
    body: String,
    tags: Set[ArticleTag],
    author: Profile,
    favorite: Boolean,
    favoritesCount: Int,
    createdAt: Instant,
    updatedAt: Instant
) extends Article {

  override val authorId: ModelId = author.id
}

case class UnsavedArticle(
    slug: Slug,
    tittle: String,
    description: String,
    body: String,
    tags: Set[ArticleTag],
    authorId: ModelId,
    updatedAt: Instant,
    createdAt: Instant
) extends Article
