package com.melalex.realworld
package articles.model

import commons.model.ModelId

case class ArticleToTagRelation(
    articleId: ModelId,
    tagId: ModelId
)
