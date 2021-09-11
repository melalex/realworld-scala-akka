package com.melalex.realworld
package articles.model

import commons.model.ModelId
import tags.Tag
import users.model.SavedUser

import java.time.Instant

trait Article {

  val slug: String
  val tittle: String
  val description: String
  val body: String
  val tagList: List[Tag]
  val createdAt: Instant
  val updatedAt: Instant
  val authorId: ModelId
}

case class SavedArticle(
    id: ModelId,
    slug: String,
    tittle: String,
    description: String,
    body: String,
    tagList: List[Tag],
    createdAt: Instant,
    updatedAt: Instant,
    author: SavedUser
) extends Article {

  override val authorId: ModelId = author.id
}

case class UnsavedArticle(
    slug: String,
    tittle: String,
    description: String,
    body: String,
    tagList: List[Tag],
    createdAt: Instant,
    updatedAt: Instant,
    authorId: ModelId
) extends Article
