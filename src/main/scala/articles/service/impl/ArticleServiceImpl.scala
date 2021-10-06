package com.melalex.realworld
package articles.service.impl

import articles.model._
import articles.repository.ArticleRepository
import articles.service.{ArticleFactory, ArticleService}
import commons.auth.model.{ActualUserPrincipal, UserPrincipal}
import commons.db.DbInterpreter
import commons.errors.model.{NotFoundException, RealWorldError, RealWorldException, SecurityException}
import commons.model.Pageable
import tags.model.ArticleTag

import cats.Monad
import cats.data.{EitherT, OptionT}
import cats.implicits._

import scala.concurrent.ExecutionContext

class ArticleServiceImpl[F[_]: Monad, DB[_]: Monad](
    articleRepository: ArticleRepository[DB],
    dbInterpreter: DbInterpreter[F, DB],
    articleFactory: ArticleFactory
)(implicit executionContext: ExecutionContext)
    extends ArticleService[F] {

  override def createArticle(params: CreateArticleParams)(implicit user: ActualUserPrincipal): F[SavedArticle] =
    dbInterpreter.executeTransitionally(articleRepository.save(articleFactory.createNew(params, user.id)))

  override def updateArticle(slug: Slug, params: UpdateArticleParams)(implicit
      user: ActualUserPrincipal
  ): F[Either[RealWorldException, SavedArticle]] = {
    val unitOfWork = for {
      article <- getBySlugInternal(slug)
      _       <- checkAccessRights(article)
      updated <-
        EitherT.right[RealWorldException](articleRepository.save(articleFactory.createUpdated(article, params)))
    } yield updated

    dbInterpreter.executeTransitionally(unitOfWork.value)
  }

  override def getAll(filter: ArticleFilter)(implicit user: UserPrincipal): F[Seq[SavedArticle]] =
    dbInterpreter.execute(articleRepository.findAll(user.id, filter))

  override def getFeed(pageable: Pageable)(implicit user: UserPrincipal): F[Seq[SavedArticle]] =
    dbInterpreter.execute(articleRepository.findAllFollowing(user.id, pageable))

  override def getBySlug(slug: Slug)(implicit user: UserPrincipal): F[Either[RealWorldException, SavedArticle]] =
    dbInterpreter.execute(getBySlugInternal(slug).value)

  override def delete(slug: Slug)(implicit user: ActualUserPrincipal): F[Unit] = {
    val unitOfWork = for {
      article <- getBySlugInternal(slug)
      _       <- checkAccessRights(article)
      _       <- EitherT.right[RealWorldException](articleRepository.delete(user.id, slug))
    } yield ()

    dbInterpreter
      .executeTransitionally(unitOfWork.value)
      .map(_ => ())
  }

  def getAllTags: F[Seq[ArticleTag]] = dbInterpreter.execute(articleRepository.findAllTags())

  override def markFavorite(slug: Slug)(implicit user: ActualUserPrincipal): F[Either[RealWorldException, Unit]] = {
    val unitOfWork = for {
      article <- getBySlugInternal(slug)
      _       <- EitherT.right[RealWorldException](articleRepository.createUserRelation(user.id, article.id))
    } yield ()

    dbInterpreter.executeTransitionally(unitOfWork.value)
  }

  override def unmarkFavorite(slug: Slug)(implicit user: ActualUserPrincipal): F[Either[RealWorldException, Unit]] = {
    val unitOfWork = for {
      article <- getBySlugInternal(slug)
      _       <- EitherT.right[RealWorldException](articleRepository.deleteUserRelation(user.id, article.id))
    } yield ()

    dbInterpreter.executeTransitionally(unitOfWork.value)
  }

  private def getBySlugInternal(slug: Slug)(implicit
      user: UserPrincipal
  ): EitherT[DB, RealWorldException, SavedArticle] = OptionT(articleRepository.findBySlug(user.id, slug))
    .toRight(RealWorldError.NotFound(slug.value).ex[NotFoundException])

  private def checkAccessRights(
      article: Article
  )(implicit user: UserPrincipal): EitherT[DB, RealWorldException, Article] = {
    val either =
      if (article.authorId == user.id) Right(article)
      else Left(RealWorldError.InsufficientAccess(user.username).ex[SecurityException])

    EitherT.fromEither[DB](either)
  }
}
