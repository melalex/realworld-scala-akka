package com.melalex.realworld
package test.util

import commons.auth.model.SecurityToken

import akka.http.scaladsl.model.headers.{Authorization, GenericHttpCredentials}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Encoder
import io.circe.syntax._

trait HttpSupport extends FailFastCirceSupport {

  protected def httpEntity[A: Encoder](entity: A): HttpEntity.Strict = HttpEntity(ContentTypes.`application/json`, entity.asJson.noSpaces)

  protected def authorization(token: SecurityToken)(implicit dummyImplicit: DummyImplicit): Authorization = authorization(token.value)

  protected def authorization(token: String): Authorization = Authorization(GenericHttpCredentials("Token", token))
}
