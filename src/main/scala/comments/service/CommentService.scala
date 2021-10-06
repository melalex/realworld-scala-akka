package com.melalex.realworld
package comments.service

import articles.model.Slug
import comments.model.SavedComment
import commons.auth.model.{ActualUserPrincipal, UserPrincipal}
import commons.errors.model.{ClientException, NotFoundException}
import commons.model.ModelId

trait CommentService[F[_]] {

  def createComment(body: String, authorId: ModelId, articleSlug: Slug): F[Either[NotFoundException, SavedComment]]

  def getAllByArticleSlug(slug: Slug)(implicit user: UserPrincipal): F[Seq[SavedComment]]

  def delete(id: ModelId)(implicit user: ActualUserPrincipal): F[Unit]
}
