package com.melalex.realworld
package commons.db

import slick.basic.DatabaseConfig
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class SlickToFutureDbInterpreter(dbConfig: DatabaseConfig[JdbcProfile]) extends DbInterpreter[Future, DBIO] {

  override def execute[A](action: DBIO[A]): Future[A] = dbConfig.db.run(action)

  override def executeTransitionally[A](action: DBIO[A]): Future[A] = {
    import dbConfig.profile.api._

    execute(action.transactionally)
  }

  override def sequence[A](action: Seq[DBIO[A]]): DBIO[Seq[A]] = DBIO.sequence(action)
}
