package com.melalex.realworld
package commons.i18n.web

import akka.http.scaladsl.model.headers.Language
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.selectPreferredLanguage

import java.util.Locale

trait I18nDirectives {

  def locale(supportedLocales: Seq[String]): Directive1[Locale] =
    selectPreferredLanguage(Language(supportedLocales.head), supportedLocales.tail.map(Language(_)): _*)
      .map(lang => Locale.forLanguageTag(lang.primaryTag))
}

object I18nDirectives extends I18nDirectives
