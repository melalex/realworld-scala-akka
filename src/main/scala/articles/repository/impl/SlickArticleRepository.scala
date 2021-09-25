package com.melalex.realworld
package articles.repository.impl

import articles.model.Slug.ModelIdMapping
import articles.model._
import articles.repository.ArticleRepository
import articles.repository.impl.SlickArticleRepository.{ArticleToTagRelations, Articles, DbArticle}
import commons.db.{DBIOInstances, DbInitRequired, Droppable}
import commons.errors.model.{RealWorldError, ServerException}
import commons.model.{ModelId, Pageable}
import tags.model.SavedArticleTag
import tags.repository.TagRepository
import tags.repository.impl.SlickTagRepository.ArticleTags
import users.repository.UserRepository
import users.repository.impl.SlickUserRepository.Users

import cats.data.OptionT
import com.melalex.realworld.commons.db.SlickSchemas.{ArticleToTagRelations, Users}
import slick.dbio.DBIO
import slick.jdbc.MySQLProfile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}
import slick.lifted._

import java.time.Instant
import scala.concurrent.ExecutionContext

class SlickArticleRepository(
    tagRepository: TagRepository[DBIO]
)(implicit ec: ExecutionContext)
    extends ArticleRepository[DBIO]
    with DbInitRequired[DBIO]
    with Droppable[DBIO]
    with DBIOInstances {

  override def init(): DBIO[Unit] = ???

  override def drop(): DBIO[Unit] = ???

  // TODO: Migrate user id -> username
  // TODO: Move all schemas to single trait

  override def save(article: Article): DBIO[SavedArticle] = {
    def createNewArticle(newArticle: UnsavedArticle) =
      for {
        articleId <- Articles.returning(Articles.map(_.id)) += toDb(newArticle)
        tags      <- tagRepository.saveAll(article.tagList)
        _         <- addTagsToArticle(articleId, tags)
        saved     <- OptionT(findBySlug(article.authorId, article.slug)).getOrElse(articleMustBePresent)
      } yield saved

    def updateExistingArticle(existingArticle: SavedArticle) = ???

    article match {
      case it: SavedArticle   => updateExistingArticle(it)
      case it: UnsavedArticle => createNewArticle(it)
    }
  }

  override def findAll(callerUserId: ModelId, params: ArticleParameters): DBIO[Seq[SavedArticle]] = ???

  override def findAllFollowing(callerUserId: ModelId, pageable: Pageable): DBIO[Seq[SavedArticle]] = ???

  override def findBySlug(callerUserId: ModelId, slug: Slug): DBIO[Option[SavedArticle]] = ???

  override def delete(slug: Slug): DBIO[Unit] = ???

  override def createUserRelation(userId: ModelId, articleId: ModelId): DBIO[UserToArticleRelation] = ???

  override def deleteUserRelation(userId: ModelId, articleId: ModelId): DBIO[Unit] = ???

  private def addTagsToArticle(article: ModelId, tags: Seq[SavedArticleTag]) = DBIO.sequence(
    tags.map(it => ArticleToTagRelations += ArticleToTagRelation(article, it.id))
  )

  private def toDb(source: UnsavedArticle) = DbArticle(
    id = ModelId.UnSaved,
    slug = source.slug,
    tittle = source.tittle,
    description = source.description,
    body = source.body,
    authorId = source.authorId,
    updatedAt = source.updatedAt,
    createdAt = source.createdAt
  )

  private def toDb(source: SavedArticle) = DbArticle(
    id = source.id,
    slug = source.slug,
    tittle = source.tittle,
    description = source.description,
    body = source.body,
    authorId = source.authorId,
    updatedAt = source.updatedAt,
    createdAt = source.createdAt
  )

  private def articleMustBePresent = {
    DBIO.failed(RealWorldError.InternalServerError.ex[ServerException])
  }
}
