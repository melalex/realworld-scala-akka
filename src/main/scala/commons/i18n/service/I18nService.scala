package com.melalex.realworld
package commons.i18n.service

import java.util.Locale

trait I18nService {

  def getMessage(key: String, args: String*)(implicit locale: Locale): String

  def getMessageOrKey(key: String)(implicit locale: Locale): String
}
