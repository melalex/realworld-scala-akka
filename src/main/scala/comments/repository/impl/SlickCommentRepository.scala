package com.melalex.realworld
package comments.repository.impl

import articles.model.Slug
import comments.model.{Comment, SavedComment, UnsavedComment}
import comments.repository.CommentRepository
import commons.db.{DBIOInstances, SlickInstances}
import commons.errors.model.{RealWorldError, ServerException}
import commons.model.ModelId
import profiles.model.Profile
import users.model.SavedUser

import cats.data.OptionT
import slick.dbio.DBIO
import slick.jdbc.MySQLProfile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}

import scala.concurrent.ExecutionContext

class SlickCommentRepository(implicit ec: ExecutionContext)
    extends CommentRepository[DBIO]
    with DBIOInstances
    with SlickInstances {

  override def save(comment: Comment): DBIO[SavedComment] = comment match {
    case it: SavedComment => comments.update(it.toDb).map(_ => it)
    case it: UnsavedComment =>
      (comments.returning(comments.map(_.id)) += it.toDb).flatMap(id => findByIdForSure(id, comment.authorId))
  }

  override def findAllBySlug(slug: Slug, callerId: ModelId): DBIO[Seq[SavedComment]] = {
    val query = for {
      (((comment, article), user), userRel) <- comments
                                                 .join(articles)
                                                 .on((c, a) => c.articleId === a.id)
                                                 .join(users)
                                                 .on((ca, u) => ca._1.authorId === u.id)
                                                 .joinLeft(userToUser)
                                                 .on((cau, uu) =>
                                                   cau._2.id === uu.followingId && uu.followerId === callerId
                                                 )
      if article.slug === slug
    } yield (comment, user, userRel.isDefined)

    query.result.map(_.map(tuple => assembleComment(tuple._1, tuple._2, tuple._3)))
  }

  override def delete(id: ModelId, authorId: ModelId): DBIO[Unit] = comments
    .filter(it => it.id === id && it.authorId === authorId)
    .delete
    .map(_ => ())

  private def findByIdForSure(id: ModelId, callerId: ModelId): DBIO[SavedComment] = OptionT(findById(id, callerId))
    .getOrElseF(DBIO.failed(RealWorldError.InternalServerError.ex[ServerException]))

  private def findById(id: ModelId, callerId: ModelId): DBIO[Option[SavedComment]] = {
    def findInternal(): DBIO[Option[(DbComment, SavedUser, Boolean)]] = {
      val query = for {
        ((comment, user), userRel) <- comments
                                        .filter(_.id === id)
                                        .join(users)
                                        .on((c, u) => c.authorId === u.id)
                                        .joinLeft(userToUser)
                                        .on((cu, uu) => cu._2.id === uu.followingId && uu.followerId === callerId)
      } yield (comment, user, userRel.isDefined)

      query.result.headOption
    }

    val cat = OptionT(findInternal()).map { case (comment, user, following) =>
      assembleComment(comment, user, following)
    }

    cat.value
  }

  private def assembleComment(base: DbComment, user: SavedUser, following: Boolean) = SavedComment(
    id = base.id,
    createdAt = base.createdAt,
    updatedAt = base.updatedAt,
    body = base.body,
    author = Profile(
      id = user.id,
      username = user.username,
      bio = user.bio,
      image = user.image,
      following = following
    ),
    articleId = base.articleId
  )
}
