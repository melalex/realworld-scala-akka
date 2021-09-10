package com.melalex.realworld
package test.util

import com.dimafeng.testcontainers.{ForAllTestContainer, MySQLContainer}
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.Suite

import scala.jdk.CollectionConverters.MapHasAsJava

trait DatabaseTestContainer extends ForAllTestContainer { self: Suite =>

  override val container: MySQLContainer = MySQLContainer(databaseName = "realworld")

  lazy val databaseConnectionConfig: Config = ConfigFactory
    .parseMap(
      Map(
        "mysql.db.url"      -> container.jdbcUrl,
        "mysql.db.user"     -> container.username,
        "mysql.db.password" -> container.password
      ).asJava
    )
}
