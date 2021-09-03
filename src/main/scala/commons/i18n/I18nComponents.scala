package com.melalex.realworld
package commons.i18n

import commons.i18n.service.I18nService
import commons.i18n.service.impl.ResourceBundleI18nService

import com.softwaremill.macwire.wire

trait I18nComponents {

  val i18nService: I18nService = wire[ResourceBundleI18nService]
}
