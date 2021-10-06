package com.melalex.realworld
package articles.service

import articles.model._
import commons.model.ModelId

import java.time.{Clock, Instant}

class ArticleFactory(clock: Clock) {

  def createNew(params: CreateArticleParams, authorId: ModelId): UnsavedArticle = UnsavedArticle(
    slug = createSlug(params.tittle),
    tittle = params.tittle,
    description = params.description,
    body = params.description,
    tags = params.tags,
    authorId = authorId,
    updatedAt = Instant.now(clock),
    createdAt = Instant.now(clock)
  )

  def createUpdated(old: SavedArticle, params: UpdateArticleParams): UnsavedArticle = UnsavedArticle(
    slug = params.tittle.map(createSlug).getOrElse(old.slug),
    tittle = params.tittle.getOrElse(old.tittle),
    description = params.description.getOrElse(old.description),
    body = params.body.getOrElse(old.body),
    tags = old.tags,
    authorId = old.authorId,
    updatedAt = Instant.now(clock),
    createdAt = old.createdAt
  )

  private def createSlug(tittle: String) = Slug(tittle.toLowerCase().replaceAll("""\s""", "-"))
}
