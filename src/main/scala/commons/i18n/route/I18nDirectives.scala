package com.melalex.realworld
package commons.i18n.route

import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.selectPreferredLanguage

import java.util.Locale

trait I18nDirectives {

  def locale(supportedLocales: Seq[String]): Directive1[Locale] =
    selectPreferredLanguage(supportedLocales.head, supportedLocales.tail)
      .map(lang => Locale.forLanguageTag(lang.primaryTag))
}

object I18nDirectives extends I18nDirectives
