package com.melalex.realworld
package commons.i18n.service.impl

import commons.i18n.service.I18nService

import java.text.MessageFormat
import java.util.{Locale, ResourceBundle}

class ResourceBundleI18nService extends I18nService {

  override def getMessage(key: String, args: String*)(implicit locale: Locale): String = {
    val format = ResourceBundle
      .getBundle("i18n/messages", locale)
      .getString(key)

    new MessageFormat(format).format(args.map(getMessageOrKey).toArray)
  }

  override def getMessageOrKey(key: String)(implicit locale: Locale): String = {
    val bundle = ResourceBundle
      .getBundle("i18n/messages", locale)

    if (bundle.containsKey(key)) bundle.getString(key)
    else key
  }
}
