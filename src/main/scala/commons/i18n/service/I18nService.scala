package com.melalex.realworld
package commons.i18n.service

import java.util.Locale

trait I18nService {

  def getMessage(key: String, args: Any*)(implicit locale: Locale): String
}
