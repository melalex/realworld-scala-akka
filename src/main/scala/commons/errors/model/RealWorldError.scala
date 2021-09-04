package com.melalex.realworld
package commons.errors.model

import commons.errors.{ClientException, NotFoundException, SecurityException, ServerException}
import commons.i18n.util.MessageKeys
import commons.model.ModelId

import scala.reflect.runtime.universe._
import scala.util.matching.Regex

sealed trait RealWorldError {

  def messageCode: String
  def arguments: Seq[String] = Seq.empty
}

object RealWorldError extends MessageKeys {

  type ExceptionFactory[A] = RealWorldError => A

  implicit val securityFactory: ExceptionFactory[SecurityException] = it => SecurityException(Seq(it))
  implicit val notFoundFactory: ExceptionFactory[NotFoundException] = it => NotFoundException(Seq(it))
  implicit val clientFactory: ExceptionFactory[ClientException]     = it => ClientException(Seq(it))
  implicit val serverFactory: ExceptionFactory[ServerException]     = it => ServerException(Seq(it))

  private val ErrorPrefix           = "error"
  private val ValidationErrorPrefix = ErrorPrefix + ".validation"

  object InternalServerError extends RealWorldError {

    override val messageCode = s"$ErrorPrefix.internal"
  }

  object InvalidToken extends RealWorldError {

    override val messageCode = s"$ErrorPrefix.token.invalid"
  }

  object UnauthorizedToken extends RealWorldError {

    override val messageCode = s"$ErrorPrefix.user.unauthorized"
  }

  object InvalidCredentials extends RealWorldError {

    override val messageCode = s"$ErrorPrefix.credentials.invalid"
  }

  final case class NotFound[A: TypeTag](id: ModelId) extends RealWorldError {

    override val messageCode: String    = s"$ErrorPrefix.notFound"
    override val arguments: Seq[String] = Seq(modelKey[A], id.value.toString)
  }

  final case class EmptyField(fieldName: String) extends RealWorldError {

    override val messageCode: String    = s"$ValidationErrorPrefix.empty"
    override val arguments: Seq[String] = Seq(fieldName)
  }

  final case class BelowMin(fieldName: String, min: Int) extends RealWorldError {

    override val messageCode: String    = s"$ValidationErrorPrefix.min"
    override val arguments: Seq[String] = Seq(fieldName, min.toString)
  }

  final case class AboveMax(fieldName: String, max: Int) extends RealWorldError {

    override val messageCode: String    = s"$ValidationErrorPrefix.max"
    override val arguments: Seq[String] = Seq(fieldName, max.toString)
  }

  final case class RegExMismatch(fieldName: String, regex: Regex) extends RealWorldError {

    override val messageCode: String    = s"$ValidationErrorPrefix.regex"
    override val arguments: Seq[String] = Seq(fieldName, regex.toString)
  }

  implicit class RealWorldErrorOps(val realWorldError: RealWorldError) extends AnyVal {

    def ex[A](implicit factory: ExceptionFactory[A]): A = factory(realWorldError)
  }
}
