package com.melalex.realworld
package tags.repository.impl

import commons.db.{DbInitRequired, Droppable}
import commons.model.ModelId
import tags.model.{ArticleTag, SavedArticleTag}
import tags.repository.TagRepository

import slick.dbio.DBIO
import slick.jdbc.MySQLProfile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}
import slick.lifted._

class SlickTagRepository extends TagRepository[DBIO] with DbInitRequired[DBIO] with Droppable[DBIO] {

  override def init(): DBIO[Unit] = ???

  override def drop(): DBIO[Unit] = ???

  override def saveAll(tags: Seq[ArticleTag]): DBIO[Seq[SavedArticleTag]] = ???

  override def findAll(): DBIO[Seq[SavedArticleTag]] = ???
}

object SlickTagRepository {

  val ArticleTags = TableQuery[ArticleTagSchema]

  class ArticleTagSchema(tag: Tag) extends Table[SavedArticleTag](tag, "tag") {

    def id    = column[ModelId]("id", O.PrimaryKey, O.AutoInc)
    def value = column[String]("value", O.Unique)

    override def * = (id, value) <> ((SavedArticleTag.apply _).tupled, SavedArticleTag.unapply)
  }
}
