package com.melalex.realworld
package users.model

import commons.model.ModelId

case class UserToArticleRelation(
    userId: ModelId,
    articleId: ModelId
)
