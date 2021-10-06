package com.melalex.realworld
package articles.dto

import tags.model.ArticleTag

case class CreateArticleParamsDto(
    tittle: String,
    description: String,
    body: String,
    tags: Option[Seq[ArticleTag]]
)
