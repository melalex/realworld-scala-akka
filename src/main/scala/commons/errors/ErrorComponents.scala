package com.melalex.realworld
package commons.errors

import commons.errors.mappers.RealWorldErrorConversions
import commons.i18n.service.I18nService

import com.softwaremill.macwire.wire

trait ErrorComponents {

  def i18nService: I18nService

  lazy val RealWorldErrorConversions: RealWorldErrorConversions = wire[RealWorldErrorConversions]
}
