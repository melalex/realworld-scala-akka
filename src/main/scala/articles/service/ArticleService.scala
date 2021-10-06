package com.melalex.realworld
package articles.service

import articles.model._
import commons.auth.model.{ActualUserPrincipal, UserPrincipal}
import commons.errors.model.RealWorldException
import commons.model.Pageable
import tags.model.ArticleTag

trait ArticleService[F[_]] {

  def createArticle(params: CreateArticleParams)(implicit user: ActualUserPrincipal): F[SavedArticle]

  def updateArticle(slug: Slug, params: UpdateArticleParams)(implicit
      user: ActualUserPrincipal
  ): F[Either[RealWorldException, SavedArticle]]

  def getAll(filter: ArticleFilter)(implicit user: UserPrincipal): F[Seq[SavedArticle]]

  def getFeed(pageable: Pageable)(implicit user: UserPrincipal): F[Seq[SavedArticle]]

  def getBySlug(slug: Slug)(implicit user: UserPrincipal): F[Either[RealWorldException, SavedArticle]]

  def getAllTags: F[Seq[ArticleTag]]

  def delete(slug: Slug)(implicit user: ActualUserPrincipal): F[Unit]

  def markFavorite(slug: Slug)(implicit user: ActualUserPrincipal): F[Either[RealWorldException, Unit]]

  def unmarkFavorite(slug: Slug)(implicit user: ActualUserPrincipal): F[Either[RealWorldException, Unit]]
}
