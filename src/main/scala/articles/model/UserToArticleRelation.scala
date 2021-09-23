package com.melalex.realworld
package articles.model

import commons.model.ModelId

case class UserToArticleRelation(
    userId: ModelId,
    articleId: ModelId
)
