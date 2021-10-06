package com.melalex.realworld
package config

import scala.concurrent.duration.FiniteDuration

case class RealWorldProperties(
    server: ServerProperties,
    session: SessionProperties,
    supportedLocales: List[String]
)

case class ServerProperties(
    host: String,
    port: Int
)

case class SessionProperties(
    ttl: FiniteDuration,
    jwtSecretKey: String,
    jwtIssuer: String
)
