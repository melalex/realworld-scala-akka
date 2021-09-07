package com.melalex.realworld
package commons.db

import cats.instances.FutureInstances
import com.softwaremill.macwire.wire
import com.typesafe.config.Config
import slick.basic.DatabaseConfig
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait DbComponents { self: FutureInstances =>

  def config: Config

  implicit def executor: ExecutionContext

  lazy val dbInterpreter: DbInterpreter[Future, DBIO] = wire[SlickToFutureDbInterpreter]
  lazy val dbBootstrap: DbBootstrap[Future, DBIO]     = wire[DbBootstrap[Future, DBIO]]

  private[db] lazy val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("mysql", config)
}
