package com.melalex.realworld
package articles.dto

import tags.model.ArticleTag

case class UpdateArticleParamsDto(
    tittle: Option[String],
    description: Option[String],
    body: Option[String],
    tags: Option[Set[ArticleTag]]
)
