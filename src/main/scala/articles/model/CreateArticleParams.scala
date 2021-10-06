package com.melalex.realworld
package articles.model

import tags.model.ArticleTag

case class CreateArticleParams(
    tittle: String,
    description: String,
    body: String,
    tags: Set[ArticleTag]
)
