package com.melalex.realworld
package commons.validation.web

import commons.errors.model.ClientException
import commons.validation.RealWorldValidation.FormValidation
import users.dto.UserRegistrationDto.validateForm

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.{FromRequestUnmarshaller, Unmarshaller}
import cats.data.Validated.{Invalid, Valid}
import cats.implicits._

import scala.concurrent.Future

trait ValidationDirectives {

  implicit class ValidationRequestMarshaller[A](um: FromRequestUnmarshaller[A]) {

    def validate(implicit validation: FormValidation[A]): Unmarshaller[HttpRequest, A] = um.flatMap { _ => _ => entity =>
      validateForm(entity) {
        case Valid(_)          => Future.successful(entity)
        case Invalid(failures) => Future.failed(ClientException(failures.toList))
      }
    }
  }

}

object ValidationDirectives extends ValidationDirectives
