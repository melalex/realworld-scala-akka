package com.melalex.realworld
package commons.db

import articles.model._
import comments.model.{SavedComment, UnsavedComment}
import commons.model.{Email, ModelId}
import tags.model.ArticleTag
import users.model.{PasswordHash, SavedUser, UserToUserRelation, Username}

import slick.jdbc.MySQLProfile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}
import slick.lifted.{ProvenShape, _}

import java.time.Instant

trait SlickInstances {

  val users         = TableQuery[UserSchema]
  val userToUser    = TableQuery[UserToUserRelationSchema]
  val articles      = TableQuery[ArticleSchema]
  val userToArticle = TableQuery[UserToArticleRelationSchema]
  val articleToTag  = TableQuery[ArticleToTagRelationSchema]
  val articleTags   = TableQuery[ArticleTagSchema]
  val comments      = TableQuery[CommentSchema]

  implicit val passwordHashMapping: BaseColumnType[PasswordHash] = MappedColumnType.base[PasswordHash, String](
    vo => vo.value,
    value => PasswordHash(value)
  )

  implicit val slugMapping: BaseColumnType[Slug] = MappedColumnType.base[Slug, String](
    vo => vo.value,
    value => Slug(value)
  )

  implicit val usernameMapping: BaseColumnType[Username] = MappedColumnType.base[Username, String](
    vo => vo.value,
    value => Username(value)
  )

  implicit val emailMapping: BaseColumnType[Email] = MappedColumnType.base[Email, String](
    vo => vo.value,
    value => Email(value)
  )

  implicit val tagMapping: BaseColumnType[ArticleTag] = MappedColumnType.base[ArticleTag, String](
    vo => vo.value,
    value => ArticleTag(value)
  )

  implicit val modelIdMapping: BaseColumnType[ModelId] = MappedColumnType.base[ModelId, Long](
    vo => vo.value,
    value => ModelId(value)
  )

  case class DbArticle(
      id: ModelId,
      slug: Slug,
      tittle: String,
      description: String,
      body: String,
      authorId: ModelId,
      updatedAt: Instant,
      createdAt: Instant
  )

  case class DbComment(
      id: ModelId,
      createdAt: Instant,
      updatedAt: Instant,
      body: String,
      authorId: ModelId,
      articleId: ModelId
  )

  case class DbTag(
      id: ModelId,
      value: ArticleTag
  )

  implicit class SavedArticleOps(self: SavedArticle) {

    def toDb: DbArticle = DbArticle(
      id = self.id,
      slug = self.slug,
      tittle = self.tittle,
      description = self.description,
      body = self.body,
      authorId = self.authorId,
      updatedAt = self.updatedAt,
      createdAt = self.createdAt
    )
  }

  implicit class UnsavedArticleOps(self: UnsavedArticle) {

    def toDb: DbArticle = DbArticle(
      id = ModelId.Unsaved,
      slug = self.slug,
      tittle = self.tittle,
      description = self.description,
      body = self.body,
      authorId = self.authorId,
      updatedAt = self.updatedAt,
      createdAt = self.createdAt
    )
  }

  implicit class ArticleTagOps(self: ArticleTag) {

    def toDb: DbTag = DbTag(
      id = ModelId.Unsaved,
      value = self
    )
  }

  implicit class UnsavedCommentOps(self: UnsavedComment) {

    def toDb: DbComment = DbComment(
      id = ModelId.Unsaved,
      createdAt = self.createdAt,
      updatedAt = self.updatedAt,
      body = self.body,
      authorId = self.authorId,
      articleId = self.articleId
    )
  }

  implicit class SavedCommentOps(self: SavedComment) {

    def toDb: DbComment = DbComment(
      id = self.id,
      createdAt = self.createdAt,
      updatedAt = self.updatedAt,
      body = self.body,
      authorId = self.authorId,
      articleId = self.articleId
    )
  }

  class UserSchema(tag: Tag) extends Table[SavedUser](tag, "user") {

    def id        = column[ModelId]("id", O.PrimaryKey, O.AutoInc)
    def email     = column[Email]("email", O.Unique, O.Length(320))
    def username  = column[Username]("username", O.Unique, O.Length(40))
    def password  = column[PasswordHash]("password", O.Length(240))
    def bio       = column[Option[String]]("bio", O.Length(254))
    def image     = column[Option[String]]("image", O.Length(320))
    def createdAt = column[Instant]("created_at")
    def updatedAt = column[Instant]("updated_at")

    override def * : ProvenShape[SavedUser] = (id, email, username, password, bio, image, createdAt, updatedAt) <>
      ((SavedUser.apply _).tupled, SavedUser.unapply)
  }

  class UserToUserRelationSchema(tag: Tag) extends Table[UserToUserRelation](tag, "user_to_user") {

    def followerId  = column[ModelId]("follower_id")
    def followingId = column[ModelId]("following_id")

    def pk        = primaryKey("pk_user_to_user", (followerId, followingId))
    def follower  = foreignKey("fk_follower", followerId, users)(_.id, onDelete = ForeignKeyAction.Cascade)
    def following = foreignKey("fk_following", followingId, users)(_.id, onDelete = ForeignKeyAction.Cascade)

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

    def author = foreignKey("fk_author", authorId, users)(_.id, onDelete = ForeignKeyAction.Cascade)

    override def * = (id, slug, tittle, description, body, authorId, updatedAt, createdAt) <>
      ((DbArticle.apply _).tupled, DbArticle.unapply)
  }

  class ArticleTagSchema(tag: Tag) extends Table[DbTag](tag, "tag") {

    def id    = column[ModelId]("id", O.PrimaryKey, O.AutoInc)
    def value = column[ArticleTag]("value", O.Unique)

    override def * = (id, value) <> ((DbTag.apply _).tupled, DbTag.unapply)
  }

  class CommentSchema(tag: Tag) extends Table[DbComment](tag, "comment") {

    def id        = column[ModelId]("id", O.PrimaryKey, O.AutoInc)
    def createdAt = column[Instant]("created_at")
    def updatedAt = column[Instant]("updated_at")
    def body      = column[String]("body")
    def authorId  = column[ModelId]("author_id")
    def articleId = column[ModelId]("article_id")

    def article = foreignKey("fk_article", articleId, articles)(_.id, onDelete = ForeignKeyAction.Cascade)
    def author  = foreignKey("fk_author", authorId, users)(_.id, onDelete = ForeignKeyAction.Cascade)

    override def * =
      (id, createdAt, updatedAt, body, authorId, articleId) <> ((DbComment.apply _).tupled, DbComment.unapply)
  }

  class UserToArticleRelationSchema(tag: Tag) extends Table[UserToArticleRelation](tag, "user_to_article") {

    def userId    = column[ModelId]("user_id")
    def articleId = column[ModelId]("article_id")

    def pk      = primaryKey("pk_user_to_article", (userId, articleId))
    def user    = foreignKey("fk_user", userId, users)(_.id, onDelete = ForeignKeyAction.Cascade)
    def article = foreignKey("fk_article", articleId, articles)(_.id, onDelete = ForeignKeyAction.Cascade)

    override def * = (userId, articleId) <> ((UserToArticleRelation.apply _).tupled, UserToArticleRelation.unapply)
  }

  class ArticleToTagRelationSchema(tag: Tag) extends Table[ArticleToTagRelation](tag, "article_to_tag") {

    def articleId = column[ModelId]("article_id")
    def tagId     = column[ModelId]("tag_id")

    def pk      = primaryKey("pk_article_to_tag", (articleId, tagId))
    def article = foreignKey("fk_article", articleId, articles)(_.id, onDelete = ForeignKeyAction.Cascade)
    def tag     = foreignKey("fk_tag", tagId, articleTags)(_.id, onDelete = ForeignKeyAction.Cascade)

    override def * = (articleId, tagId) <> ((ArticleToTagRelation.apply _).tupled, ArticleToTagRelation.unapply)
  }
}

object SlickInstances extends SlickInstances
