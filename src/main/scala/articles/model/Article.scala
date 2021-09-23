package com.melalex.realworld
package articles.model

import commons.model.ModelId
import tags.model.{ArticleTag, SavedArticleTag}
import users.model.SavedUser

import java.time.Instant

trait Article {

  val slug: Slug
  val tittle: String
  val description: String
  val body: String
  val tagList: List[ArticleTag]
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
    tagList: List[SavedArticleTag],
    author: SavedUser,
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
    tagList: List[ArticleTag],
    authorId: ModelId,
    updatedAt: Instant,
    createdAt: Instant
) extends Article
