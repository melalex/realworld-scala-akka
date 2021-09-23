package com.melalex.realworld
package tags.model

import commons.model.ModelId

trait ArticleTag {

  val value: String
}

case class SavedArticleTag(
    id: ModelId,
    value: String
) extends ArticleTag

case class UnsavedArticleTag(
    value: String
) extends ArticleTag
