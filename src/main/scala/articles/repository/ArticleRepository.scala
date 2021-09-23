package com.melalex.realworld
package articles.repository

import articles.model._
import commons.model.{ModelId, Pageable}

trait ArticleRepository[DB[_]] {

  def save(article: Article): DB[SavedArticle]

  def findAll(callerUserId: ModelId, params: ArticleParameters): DB[Seq[SavedArticle]]

  def findAllFollowing(callerUserId: ModelId, pageable: Pageable): DB[Seq[SavedArticle]]

  def findBySlug(callerUserId: ModelId, slug: Slug): DB[Option[SavedArticle]]

  def delete(slug: Slug): DB[Unit]

  def createUserRelation(userId: ModelId, articleId: ModelId): DB[UserToArticleRelation]

  def deleteUserRelation(userId: ModelId, articleId: ModelId): DB[Unit]
}
