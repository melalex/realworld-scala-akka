package com.melalex.realworld
package fixture

import commons.db._

import cats.instances.FutureInstances
import com.dimafeng.testcontainers.{ForAllTestContainer, MySQLContainer}
import com.softwaremill.macwire.wire
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.Suite
import slick.basic.DatabaseConfig
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._

trait DatabaseTestKit extends ForAllTestContainer with FutureInstances { self: Suite =>

  implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  override val container: MySQLContainer = MySQLContainer(databaseName = "realworld")

  lazy val dbConfigMap = Map(
    "mysql.db.url"      -> container.jdbcUrl,
    "mysql.db.user"     -> container.username,
    "mysql.db.password" -> container.password
  )

  lazy val config: Config =
    ConfigFactory
      .parseMap(dbConfigMap.asJava)
      .withFallback(ConfigFactory.load())

  lazy val dbInterpreter: DbInterpreter[Future, DBIO] = wire[SlickToFutureDbInterpreter]
  lazy val dbBootstrap: DbBootstrap[Future, DBIO]     = wire[DbBootstrap[Future, DBIO]]

  lazy val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("mysql", config)
}
