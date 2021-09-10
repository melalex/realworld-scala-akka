package com.melalex.realworld
package test.util

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

trait FeatureSupport extends ScalaFutures {

  implicit val customPatience: PatienceConfig = PatienceConfig(timeout = Span(5, Seconds), interval = Span(5, Millis))
}
