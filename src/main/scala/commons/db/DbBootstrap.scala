package com.melalex.realworld
package commons.db

import cats.Monad
import cats.implicits._
import slick.dbio.DBIO
import slick.jdbc.MySQLProfile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}

class DbBootstrap[F[_]: Monad](dbInterpreter: DbInterpreter[F, DBIO]) extends SlickInstances {

  def init(): F[Unit] = {
    val initScript = DBIO.sequence(
      Seq(
        users.schema.createIfNotExists,
        articles.schema.createIfNotExists,
        articleTags.schema.createIfNotExists,
        comments.schema.createIfNotExists,
        userToUser.schema.createIfNotExists,
        userToArticle.schema.createIfNotExists,
        articleToTag.schema.createIfNotExists
      )
    )

    dbInterpreter.executeTransitionally(initScript).map(_ => ())
  }

  def drop(): F[Unit] = {
    val initScript = DBIO.sequence(
      Seq(
        articleToTag.schema.dropIfExists,
        userToUser.schema.dropIfExists,
        userToArticle.schema.dropIfExists,
        comments.schema.dropIfExists,
        articles.schema.dropIfExists,
        articleTags.schema.dropIfExists,
        users.schema.dropIfExists
      )
    )

    dbInterpreter.executeTransitionally(initScript).map(_ => ())
  }
}
