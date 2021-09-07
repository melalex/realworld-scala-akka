package com.melalex.realworld
package config

import java.time.Duration

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
    ttl: Duration,
    jwtSecretKey: String,
    jwtIssuer: String
)
