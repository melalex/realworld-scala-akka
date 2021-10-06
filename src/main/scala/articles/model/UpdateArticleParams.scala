package com.melalex.realworld
package articles.model

import tags.model.ArticleTag

case class UpdateArticleParams(
    tittle: Option[String],
    description: Option[String],
    body: Option[String],
    tags: Option[Set[ArticleTag]]
)
