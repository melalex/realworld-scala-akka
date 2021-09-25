package com.melalex.realworld
package commons.db

import articles.model.{ArticleToTagRelation, DbArticle, Slug, UserToArticleRelation}
import commons.model.ModelId
import tags.repository.impl.SlickTagRepository.ArticleTags
import users.model.{PasswordHash, SavedUser, UserToUserRelation}

import slick.jdbc.MySQLProfile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}
import slick.lifted.{ProvenShape, _}

import java.time.Instant

trait SlickSchemas {

  val Users                  = TableQuery[UserSchema]
  val UserToUserRelations    = TableQuery[UserToUserRelationSchema]
  val Articles               = TableQuery[ArticleSchema]
  val UserToArticleRelations = TableQuery[UserToArticleRelationSchema]
  val ArticleToTagRelations  = TableQuery[ArticleToTagRelationSchema]

  implicit private val passwordHashMapping: BaseColumnType[PasswordHash] =
    MappedColumnType.base[PasswordHash, String](
      vo => vo.value,
      id => PasswordHash(id)
    )

  implicit private val slugMapping: BaseColumnType[Slug] =
    MappedColumnType.base[Slug, String](
      vo => vo.value,
      id => Slug(id)
    )

  implicit private val modelIdMapping: BaseColumnType[ModelId] =
    MappedColumnType.base[ModelId, Long](
      vo => vo.value,
      id => ModelId(id)
    )

  class UserSchema(tag: Tag) extends Table[SavedUser](tag, "user") {

    def id        = column[ModelId]("id", O.PrimaryKey, O.AutoInc)
    def email     = column[String]("email", O.Unique, O.Length(320))
    def username  = column[String]("username", O.Unique, O.Length(40))
    def password  = column[PasswordHash]("password", O.Length(240))
    def bio       = column[Option[String]]("bio", O.Length(254))
    def image     = column[Option[String]]("image", O.Length(320))
    def createdAt = column[Instant]("created_at")
    def updatedAt = column[Instant]("updated_at")

    override def * : ProvenShape[SavedUser] =
      (id, email, username, password, bio, image, createdAt, updatedAt) <>
        ((SavedUser.apply _).tupled, SavedUser.unapply)
  }

  class UserToUserRelationSchema(tag: Tag) extends Table[UserToUserRelation](tag, "user_to_user") {

    def followerId  = column[ModelId]("follower_id")
    def followingId = column[ModelId]("following_id")

    def pk        = primaryKey("pk_user_to_user", (followerId, followingId))
    def follower  = foreignKey("fk_follower", followerId, Users)(_.id, onDelete = ForeignKeyAction.Cascade)
    def following = foreignKey("fk_following", followingId, Users)(_.id, onDelete = ForeignKeyAction.Cascade)

    override def * = (followerId, followingId) <> ((UserToUserRelation.apply _).tupled, UserToUserRelation.unapply)
  }

  class ArticleSchema(tag: Tag) extends Table[DbArticle](tag, "article") {

    def id          = column[ModelId]("id", O.PrimaryKey, O.AutoInc)
    def slug        = column[Slug]("slug", O.Unique, O.Length(400))
    def tittle      = column[String]("tittle")
    def description = column[String]("description")
    def body        = column[String]("body")
    def authorId    = column[ModelId]("author_id")
    def updatedAt   = column[Instant]("updated_at")
    def createdAt   = column[Instant]("created_at")

    def author = foreignKey("fk_author", authorId, Users)(_.id, onDelete = ForeignKeyAction.Cascade)

    override def * =
      (id, slug, tittle, description, body, authorId, updatedAt, createdAt) <>
        ((DbArticle.apply _).tupled, DbArticle.unapply)
  }

  class UserToArticleRelationSchema(tag: Tag) extends Table[UserToArticleRelation](tag, "user_to_article") {

    def userId    = column[ModelId]("user_id")
    def articleId = column[ModelId]("article_id")

    def pk      = primaryKey("pk_user_to_article", (userId, articleId))
    def user    = foreignKey("fk_user", userId, Users)(_.id, onDelete = ForeignKeyAction.Cascade)
    def article = foreignKey("fk_article", articleId, Articles)(_.id, onDelete = ForeignKeyAction.Cascade)

    override def * = (userId, articleId) <> ((UserToArticleRelation.apply _).tupled, UserToArticleRelation.unapply)
  }

  class ArticleToTagRelationSchema(tag: Tag) extends Table[ArticleToTagRelation](tag, "article_to_tag") {

    def articleId = column[ModelId]("article_id")
    def tagId     = column[ModelId]("tag_id")

    def pk      = primaryKey("pk_article_to_tag", (articleId, tagId))
    def article = foreignKey("fk_article", articleId, Articles)(_.id, onDelete = ForeignKeyAction.Cascade)
    def tag     = foreignKey("fk_tag", tagId, ArticleTags)(_.id, onDelete = ForeignKeyAction.Cascade)

    override def * = (articleId, tagId) <> ((ArticleToTagRelation.apply _).tupled, ArticleToTagRelation.unapply)
  }
}

object SlickSchemas extends SlickSchemas
