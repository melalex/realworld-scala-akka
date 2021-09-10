package com.melalex.realworld
package test.spec

import test.util.HttpSupport

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.mockito.IdiomaticMockito

trait RouteSpec extends UnitTestSpec with IdiomaticMockito with ScalatestRouteTest with HttpSupport
