package com.melalex.realworld
package fixture

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterEach, OptionValues}

import scala.concurrent.ExecutionContext

trait RealWorldSpec extends AnyFlatSpec with Matchers with ScalaFutures with OptionValues with BeforeAndAfterEach {

  implicit val customPatience: PatienceConfig     = PatienceConfig(timeout = Span(5, Seconds), interval = Span(5, Millis))
  implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}
