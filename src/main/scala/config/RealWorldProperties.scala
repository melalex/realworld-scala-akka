package com.melalex.realworld
package config

case class RealWorldProperties(
    server: ServerProperties,
    supportedLocales: List[String]
)

case class ServerProperties(host: String, port: Int)
