package com.melalex.realworld
package articles.model

import commons.model.ModelId
import tags.Tag

case class ArticleToTagRelation(
    articleId: ModelId,
    tag: Tag
)
