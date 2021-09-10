package com.melalex.realworld
package commons.web.impl

import commons.errors.dto.RealWorldErrorDto
import commons.errors.mappers.RealWorldErrorConversions
import commons.errors.model._
import commons.web.RouteProvider
import test.fixture.PropertiesFixture
import test.spec.RouteSpec

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.{Language, `Accept-Language`}
import akka.http.scaladsl.server.Directives
import org.mockito.ArgumentMatchersSugar

import java.util.Locale

class CompositeRouteProviderSuite extends RouteSpec with PropertiesFixture with ArgumentMatchersSugar with Directives {

  private implicit val locale: Locale = Locale.ENGLISH

  private val errorMessage = "Generic error"

  private val delegate         = mock[RouteProvider]
  private val errorConversions = mock[RealWorldErrorConversions]

  private val compositeRouteProvider = new CompositeRouteProvider(Set(delegate), errorConversions, realWorldProperties)

  override protected def beforeEach(): Unit = {
    errorConversions.toDto(any[Seq[RealWorldError]]) returns RealWorldErrorDto(Seq(errorMessage))
  }

  "CompositeRouteProvider" should "handle NotFoundException" in {
    delegate.provideRoute returns get {
      failWith(NotFoundException(Seq()))
    }

    Get() ~> `Accept-Language`(Language(locale.getLanguage)) ~> compositeRouteProvider.provideRoute ~> check {
      status shouldEqual StatusCodes.NotFound
    }
  }

  it should "handle CredentialsException" in {
    delegate.provideRoute returns get {
      failWith(CredentialsException(Seq()))
    }

    Get() ~> `Accept-Language`(Language(locale.getLanguage)) ~> compositeRouteProvider.provideRoute ~> check {
      status shouldEqual StatusCodes.Unauthorized
    }
  }

  it should "handle ClientException" in {
    delegate.provideRoute returns get {
      failWith(ClientException(Seq()))
    }

    Get() ~> `Accept-Language`(Language(locale.getLanguage)) ~> compositeRouteProvider.provideRoute ~> check {
      status shouldEqual StatusCodes.BadRequest
    }
  }

  it should "handle ServerException" in {
    delegate.provideRoute returns get {
      failWith(ServerException(Seq()))
    }

    Get() ~> `Accept-Language`(Language(locale.getLanguage)) ~> compositeRouteProvider.provideRoute ~> check {
      status shouldEqual StatusCodes.InternalServerError
    }
  }

  it should "handle generic exception" in {
    delegate.provideRoute returns get {
      failWith(new Throwable())
    }

    Get() ~> `Accept-Language`(Language(locale.getLanguage)) ~> compositeRouteProvider.provideRoute ~> check {
      status shouldEqual StatusCodes.InternalServerError
    }
  }
}
