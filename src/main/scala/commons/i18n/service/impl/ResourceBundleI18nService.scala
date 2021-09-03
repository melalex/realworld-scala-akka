package com.melalex.realworld
package commons.i18n.service.impl

import commons.i18n.service.I18nService

import java.text.MessageFormat
import java.util.{Locale, ResourceBundle}

class ResourceBundleI18nService extends I18nService {

  override def getMessage(key: String, args: Any*)(implicit locale: Locale): String = {
    val format = ResourceBundle
      .getBundle("i18n/messages", locale)
      .getString(key)

    new MessageFormat(format).format(args)
  }
}
