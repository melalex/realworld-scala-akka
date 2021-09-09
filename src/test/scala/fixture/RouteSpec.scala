package com.melalex.realworld
package fixture

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Encoder
import io.circe.syntax._
import org.mockito.IdiomaticMockito

trait RouteSpec extends RealWorldSpec with IdiomaticMockito with ScalatestRouteTest with FailFastCirceSupport {

  def httpEntity[A: Encoder](entity: A): HttpEntity.Strict = HttpEntity(ContentTypes.`application/json`, entity.asJson.noSpaces)
}
