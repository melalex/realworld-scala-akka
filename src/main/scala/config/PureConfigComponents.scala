package com.melalex.realworld
package config

import com.typesafe.config.{Config, ConfigFactory}
import pureconfig._
import pureconfig.generic.ExportMacros

import scala.language.experimental.macros

trait PureConfigComponents {

  implicit val configSource: ConfigObjectSource = ConfigSource.default

  def config: Config = ConfigFactory.load()

  implicit def exportReader[A]: Exported[ConfigReader[A]] =
    macro ExportMacros.exportDerivedReader[A]

  implicit def exportWriter[A]: Exported[ConfigWriter[A]] =
    macro ExportMacros.exportDerivedWriter[A]
}
