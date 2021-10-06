package com.melalex.realworld
package articles.model

import commons.model.Pageable
import tags.model.ArticleTag
import users.model.Username

case class ArticleFilter(
    tag: Option[ArticleTag],
    author: Option[Username],
    favorited: Boolean,
    pageable: Pageable
)

object ArticleFilter {

  def apply(tag: Option[String], author: Option[String], favorited: Boolean, limit: Long, offset: Long): ArticleFilter =
    ArticleFilter(tag.map(ArticleTag), author.map(Username), favorited, Pageable(limit, offset))
}
