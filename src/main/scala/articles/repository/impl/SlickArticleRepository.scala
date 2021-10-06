package com.melalex.realworld
package articles.repository.impl

import articles.model._
import articles.repository.ArticleRepository
import commons.db.{DBIOInstances, SlickInstances}
import commons.errors.model.{RealWorldError, ServerException}
import commons.model.{ModelId, Pageable}
import profiles.model.Profile
import tags.model.ArticleTag
import users.model.{SavedUser, UserToUserRelation}

import cats.data._
import slick.dbio.DBIO
import slick.jdbc.MySQLProfile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}

import scala.concurrent.ExecutionContext

class SlickArticleRepository(implicit ec: ExecutionContext)
    extends ArticleRepository[DBIO]
    with DBIOInstances
    with SlickInstances {

  override def save(article: Article): DBIO[SavedArticle] = {
    def createNewArticle(newArticle: UnsavedArticle) = for {
      articleId <- articles.returning(articles.map(_.id)) += newArticle.toDb
      _         <- addTagsToArticle(articleId, newArticle.tags)
    } yield ()

    def updateExistingArticle(existingArticle: SavedArticle) = for {
      _ <- articles.update(existingArticle.toDb)
      _ <- articleToTag.filter(_.articleId === existingArticle.id).delete
      _ <- addTagsToArticle(existingArticle.id, existingArticle.tags)
    } yield ()

    val saveIo = article match {
      case it: SavedArticle   => updateExistingArticle(it)
      case it: UnsavedArticle => createNewArticle(it)
    }

    saveIo.flatMap(_ => findBySlugForSure(article.authorId, article.slug))
  }

  override def findIdBySlug(slug: Slug): DBIO[Option[ModelId]] = articles
    .filter(_.slug === slug)
    .map(_.id)
    .result
    .headOption

  override def findAll(callerUserId: ModelId, params: ArticleFilter): DBIO[Seq[SavedArticle]] = {
    val flatArticleData
        : DBIO[Seq[(DbArticle, Option[UserToArticleRelation], SavedUser, Option[UserToUserRelation])]] = articles
      .filterOpt(params.tag)((a, v) =>
        a.id in articleTags
          .filter(tag => tag.value === v)
          .join(articleToTag)
          .on((t, r) => t.id === r.tagId)
          .map(_._2.articleId)
      )
      .joinLeft(userToArticle)
      .on((a, r) => a.id === r.articleId && r.userId === callerUserId)
      .join(users)
      .on((a, u) => a._1.authorId === u.id)
      .joinLeft(userToUser)
      .on((u, r) => u._1._1.authorId === r.followingId && r.followerId === callerUserId)
      .map { case (((article, userToArticle), user), userToUser) => (article, userToArticle, user, userToUser) }
      .filterOpt(params.author)((row, username) => row._3.username === username)
      .filter(row => row._2.isDefined === params.favorited)
      .drop(params.pageable.offset)
      .take(params.pageable.limit)
      .result

    flatArticleData.flatMap { articles =>
      val ios = articles.map { case (article, userToArticle, user, userToUser) =>
        for {
          favoritesCount <- countFavorites(article.id)
          tags           <- findArticleTags(article.id)
        } yield assembleArticle(
          base = article,
          user = user,
          tags = tags,
          favoritesCount = favoritesCount,
          isFollowing = userToUser.isDefined,
          isFavorite = userToArticle.isDefined
        )
      }

      DBIO.sequence(ios)
    }
  }

  override def findAllFollowing(callerUserId: ModelId, pageable: Pageable): DBIO[Seq[SavedArticle]] = {
    val flatArticleData: DBIO[Seq[(DbArticle, Option[UserToArticleRelation], SavedUser)]] = articles
      .joinLeft(userToArticle)
      .on((a, r) => a.id === r.articleId && r.userId === callerUserId)
      .join(users)
      .on((a, u) => a._1.authorId === u.id)
      .join(userToUser)
      .on((u, r) => u._1._1.authorId === r.followingId && r.followerId === callerUserId)
      .map { case (((article, userToArticle), user), _) => (article, userToArticle, user) }
      .drop(pageable.offset)
      .take(pageable.limit)
      .result

    flatArticleData.flatMap { articles =>
      val ios = articles.map { case (article, userToArticle, user) =>
        for {
          favoritesCount <- countFavorites(article.id)
          tags           <- findArticleTags(article.id)
        } yield assembleArticle(
          base = article,
          user = user,
          tags = tags,
          favoritesCount = favoritesCount,
          isFollowing = true,
          isFavorite = userToArticle.isDefined
        )
      }

      DBIO.sequence(ios)
    }
  }

  override def findBySlug(callerUserId: ModelId, slug: Slug): DBIO[Option[SavedArticle]] = {
    def findArticleInternal()
        : DBIO[Option[(DbArticle, Option[UserToArticleRelation], SavedUser, Option[UserToUserRelation])]] = articles
      .filter(_.slug === slug)
      .joinLeft(userToArticle)
      .on((a, r) => a.id === r.articleId && r.userId === callerUserId)
      .join(users)
      .on((a, u) => a._1.authorId === u.id)
      .joinLeft(userToUser)
      .on((u, r) => u._1._1.authorId === r.followingId && r.followerId === callerUserId)
      .map { case (((article, userToArticle), user), userToUser) => (article, userToArticle, user, userToUser) }
      .result
      .headOption

    def countFavoritesInternal(id: ModelId): DBIO[Option[Int]] = countFavorites(id).map(Some(_))

    def findArticleTagsInternal(id: ModelId): DBIO[Option[Set[ArticleTag]]] = findArticleTags(id).map(Some(_))

    val catsOption =
      for {
        (article, userToArticle, user, userToUser) <- OptionT(findArticleInternal())
        favoritesCount                             <- OptionT(countFavoritesInternal(article.id))
        tags                                       <- OptionT(findArticleTagsInternal(article.id))
      } yield assembleArticle(
        base = article,
        user = user,
        tags = tags,
        favoritesCount = favoritesCount,
        isFollowing = userToUser.isDefined,
        isFavorite = userToArticle.isDefined
      )

    catsOption.value
  }

  override def findAllTags(): DBIO[Seq[ArticleTag]] = articleTags.map(_.value).result

  override def delete(authorId: ModelId, slug: Slug): DBIO[Unit] = articles
    .filter(it => it.slug === slug && it.authorId === authorId)
    .delete
    .map(_ => ())

  override def createUserRelation(userId: ModelId, articleId: ModelId): DBIO[UserToArticleRelation] = {
    val toAdd = UserToArticleRelation(userId, articleId)

    userToArticle
      .insertOrUpdate(toAdd)
      .map(_ => toAdd)
  }

  override def deleteUserRelation(userId: ModelId, articleId: ModelId): DBIO[Unit] = userToArticle
    .filter(it => it.userId === userId && it.articleId === articleId)
    .delete
    .map(_ => ())

  private def findBySlugForSure(callerUserId: ModelId, slug: Slug): DBIO[SavedArticle] =
    OptionT(findBySlug(callerUserId, slug))
      .getOrElseF(DBIO.failed(RealWorldError.InternalServerError.ex[ServerException]))

  private def addTagsToArticle(articleId: ModelId, tags: Set[ArticleTag]) = for {
    oldTags <- articleTags.filter(_.value.inSet(tags)).result.map(_.toSet)
    newTags <- articleTags.returning(articleTags.map(_.id)) ++= tags.diff(oldTags.map(_.value)).map(_.toDb)
    _       <- articleToTag ++= newTags.appendedAll(oldTags.map(_.id)).map(ArticleToTagRelation(articleId, _))
  } yield ()

  private def countFavorites(articleId: ModelId): DBIO[Int] = userToArticle
    .filter(_.articleId === articleId)
    .length
    .result

  private def findArticleTags(articleId: ModelId): DBIO[Set[ArticleTag]] = articleToTag
    .filter(_.articleId === articleId)
    .join(articleTags)
    .on((rel, tag) => rel.tagId === tag.id)
    .result
    .map(_.map(_._2.value).toSet)

  private def assembleArticle(
      base: DbArticle,
      user: SavedUser,
      tags: Set[ArticleTag],
      favoritesCount: Int,
      isFollowing: Boolean,
      isFavorite: Boolean
  ) = SavedArticle(
    id = base.id,
    slug = base.slug,
    tittle = base.tittle,
    description = base.description,
    body = base.body,
    tags = tags,
    author = Profile(
      id = user.id,
      username = user.username,
      bio = user.bio,
      image = user.image,
      following = isFollowing
    ),
    favorite = isFavorite,
    favoritesCount = favoritesCount,
    createdAt = base.createdAt,
    updatedAt = base.updatedAt
  )
}
