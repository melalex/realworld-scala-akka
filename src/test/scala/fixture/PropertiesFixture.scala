package com.melalex.realworld
package fixture

import config.{RealWorldProperties, ServerProperties, SessionProperties}

import java.time.Duration

trait PropertiesFixture {

  val realWorldProperties: RealWorldProperties = RealWorldProperties(
    ServerProperties(
      host = "localhost",
      port = 8080
    ),
    SessionProperties(
      ttl = Duration.ofHours(12),
      jwtSecretKey = "realworld-secret-key",
      jwtIssuer = "realworld"
    ),
    List("en")
  )
}
