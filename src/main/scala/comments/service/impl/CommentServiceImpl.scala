package com.melalex.realworld
package comments.service.impl

import articles.model.{Article, Slug}
import articles.repository.ArticleRepository
import comments.model.SavedComment
import comments.repository.CommentRepository
import comments.service.{CommentFactory, CommentService}
import commons.auth.model.{ActualUserPrincipal, UserPrincipal}
import commons.db.DbInterpreter
import commons.errors.model.{ClientException, NotFoundException, RealWorldError}
import commons.model.ModelId

import cats.Monad
import cats.data._

class CommentServiceImpl[F[_]: Monad, DB[_]: Monad](
    commentFactory: CommentFactory,
    commentRepository: CommentRepository[DB],
    articleRepository: ArticleRepository[DB],
    dbInterpreter: DbInterpreter[F, DB]
) extends CommentService[F] {

  override def createComment(
      body: String,
      authorId: ModelId,
      articleSlug: Slug
  ): F[Either[NotFoundException, SavedComment]] = {
    val unitOfWork = OptionT(articleRepository.findIdBySlug(articleSlug))
      .map(id => commentFactory.createNew(body, authorId, id))
      .semiflatMap(commentRepository.save)
      .toRight(RealWorldError.NotFound[Article](articleSlug.value).ex[NotFoundException])

    dbInterpreter.executeTransitionally(unitOfWork.value)
  }

  override def getAllByArticleSlug(slug: Slug)(implicit
      user: UserPrincipal
  ): F[Seq[SavedComment]] = dbInterpreter.execute(commentRepository.findAllBySlug(slug, user.id))

  override def delete(id: ModelId)(implicit user: ActualUserPrincipal): F[Unit] =
    dbInterpreter.executeTransitionally(commentRepository.delete(id, user.id))
}
