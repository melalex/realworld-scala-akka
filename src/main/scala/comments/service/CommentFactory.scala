package com.melalex.realworld
package comments.service

import comments.model.UnsavedComment
import commons.model.ModelId

import java.time.{Clock, Instant}

class CommentFactory(clock: Clock) {

  def createNew(body: String, authorId: ModelId, articleId: ModelId): UnsavedComment = UnsavedComment(
    createdAt = Instant.now(clock),
    updatedAt = Instant.now(clock),
    body = body,
    authorId = authorId,
    articleId = articleId
  )
}
