package com.melalex.realworld
package test.fixture

import config.{RealWorldProperties, ServerProperties, SessionProperties}

import java.time.Duration
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

trait PropertiesFixture {

  val realWorldProperties: RealWorldProperties = RealWorldProperties(
    ServerProperties(
      host = "localhost",
      port = 8080
    ),
    SessionProperties(
      ttl = 12 hours,
      jwtSecretKey = "realworld-secret-key",
      jwtIssuer = "realworld"
    ),
    List("en")
  )
}
