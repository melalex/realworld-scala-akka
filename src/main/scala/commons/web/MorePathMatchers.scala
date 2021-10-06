package com.melalex.realworld
package commons.web

import articles.model.Slug
import commons.model.ModelId
import users.model.Username

import akka.http.scaladsl.server.PathMatcher1
import akka.http.scaladsl.server.PathMatchers.{LongNumber, Segment}

trait MorePathMatchers {

  val usernameMatcher: PathMatcher1[Username] = Segment.map(Username)

  val slugMatcher: PathMatcher1[Slug] = Segment.map(Slug)

  val modelIdMatcher: PathMatcher1[ModelId] = LongNumber.map(ModelId(_))
}

object MorePathMatchers extends MorePathMatchers
