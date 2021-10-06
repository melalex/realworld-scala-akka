package com.melalex.realworld
package test.spec

import config.AppComponents
import test.util.{DatabaseTestContainer, FeatureSupport, HttpSupport, TimeSupport}

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.http.scaladsl.unmarshalling.FromResponseUnmarshaller
import akka.testkit.TestDuration
import com.typesafe.config.Config
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{Assertion, BeforeAndAfterEach, GivenWhenThen}

import java.time.{Clock, ZoneOffset}
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.reflect.ClassTag

abstract class IntegrationTestSpec
    extends AnyFeatureSpec
    with AppComponents
    with DatabaseTestContainer
    with GivenWhenThen
    with ScalatestRouteTest
    with Matchers
    with BeforeAndAfterEach
    with HttpSupport
    with FeatureSupport {

  implicit val timeout: RouteTestTimeout = RouteTestTimeout(5.seconds.dilated)

  override lazy val clock: Clock = Clock.fixed(TimeSupport.Now, ZoneOffset.UTC)

  override def config: Config = databaseConnectionConfig.withFallback(super.config)

  override protected def beforeEach(): Unit = Await.ready(dbBootstrap.init(), 5 seconds)
  override protected def afterEach(): Unit  = Await.ready(dbBootstrap.drop(), 5 seconds)

  protected def isOk: RouteTestResult => Assertion = check {
    status shouldEqual StatusCodes.OK
  }

  protected def assertBody[A: FromResponseUnmarshaller: ClassTag](
      statusCode: StatusCode = StatusCodes.OK
  ): RouteTestResult => A = check {
    status shouldEqual statusCode
    responseAs[A]
  }
}
