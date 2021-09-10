package com.melalex.realworld
package test.spec

import test.util.FeatureSupport

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}

import scala.concurrent.ExecutionContext

trait UnitTestSpec extends AnyFlatSpec with Matchers with FeatureSupport with OptionValues with BeforeAndAfterEach {

  implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}
