package com.melalex.realworld
package tags.repository

import tags.model.{ArticleTag, SavedArticleTag}

trait TagRepository[DB[_]] {

  def saveAll(tags: Seq[ArticleTag]): DB[Seq[SavedArticleTag]]

  def findAll(): DB[Seq[SavedArticleTag]]
}
