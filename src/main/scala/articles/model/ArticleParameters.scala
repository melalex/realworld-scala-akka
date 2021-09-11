package com.melalex.realworld
package articles.model

import commons.model.Pageable

case class ArticleParameters(
    tag: Option[String],
    author: Option[String],
    favorited: Boolean,
    pageable: Pageable
)

object ArticleParameters {

  def apply(tag: Option[String], author: Option[String], favorited: Boolean, limit: Long, offset: Long): ArticleParameters =
    ArticleParameters(tag, author, favorited, Pageable(limit, offset))
}
