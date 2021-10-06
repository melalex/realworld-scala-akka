package com.melalex.realworld
package comments.repository

import articles.model.Slug
import comments.model.{Comment, SavedComment}
import commons.model.ModelId

trait CommentRepository[DB[_]] {

  def save(comment: Comment): DB[SavedComment]

  def findAllBySlug(slug: Slug, callerId: ModelId): DB[Seq[SavedComment]]

  def delete(id: ModelId, authorId: ModelId): DB[Unit]
}
