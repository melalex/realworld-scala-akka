package com.melalex.realworld
package test.spec

import commons.db._
import test.util.DatabaseTestContainer

import cats.instances.FutureInstances
import com.softwaremill.macwire.wire
import com.typesafe.config.{Config, ConfigFactory}
import slick.basic.DatabaseConfig
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

trait RepositorySpec extends UnitTestSpec with DatabaseTestContainer with FutureInstances {

  lazy val config: Config                        = databaseConnectionConfig.withFallback(ConfigFactory.load())
  lazy val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("mysql", config)

  lazy val dbInterpreter: DbInterpreter[Future, DBIO] = wire[SlickToFutureDbInterpreter]
  lazy val dbBootstrap: DbBootstrap[Future]           = wire[DbBootstrap[Future]]

  override protected def beforeEach(): Unit = Await.ready(dbBootstrap.init(), 5 seconds)
  override protected def afterEach(): Unit  = Await.ready(dbBootstrap.drop(), 5 seconds)
}
