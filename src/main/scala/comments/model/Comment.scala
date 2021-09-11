package com.melalex.realworld
package comments.model

import commons.model.ModelId
import users.model.SavedUser

import java.time.Instant

trait Comment {

  val createdAt: Instant
  val updatedAt: Instant
  val body: String
  val authorId: ModelId
}

case class SavedComment(
    id: ModelId,
    createdAt: Instant,
    updatedAt: Instant,
    body: String,
    author: SavedUser
) extends Comment {

  override val authorId: ModelId = author.id
}

case class UnsavedComment(
    createdAt: Instant,
    updatedAt: Instant,
    body: String,
    authorId: ModelId
) extends Comment
