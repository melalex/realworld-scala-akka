package com.melalex.realworld
package articles.repository

import articles.model._
import commons.model.{ModelId, Pageable}
import tags.model.ArticleTag

trait ArticleRepository[DB[_]] {

  def save(article: Article): DB[SavedArticle]

  def findIdBySlug(slug: Slug): DB[Option[ModelId]]

  def findAll(callerUserId: ModelId, params: ArticleFilter): DB[Seq[SavedArticle]]

  def findAllFollowing(callerUserId: ModelId, pageable: Pageable): DB[Seq[SavedArticle]]

  def findBySlug(callerUserId: ModelId, slug: Slug): DB[Option[SavedArticle]]

  def findAllTags(): DB[Seq[ArticleTag]]

  def delete(authorId: ModelId, slug: Slug): DB[Unit]

  def createUserRelation(userId: ModelId, articleId: ModelId): DB[UserToArticleRelation]

  def deleteUserRelation(userId: ModelId, articleId: ModelId): DB[Unit]
}
