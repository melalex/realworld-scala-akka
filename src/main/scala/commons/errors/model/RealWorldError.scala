package com.melalex.realworld
package commons.errors.model

import commons.errors.{ClientException, NotFoundException, SecurityException, ServerException}
import commons.model.ModelId

import scala.reflect.runtime.universe._

case class RealWorldError private (
    messageCode: String,
    arguments: Any*
)

object RealWorldError {

  type ExceptionFactory[A] = RealWorldError => A

  implicit val securityFactory: ExceptionFactory[SecurityException] = it => SecurityException(Seq(it))
  implicit val notFoundFactory: ExceptionFactory[NotFoundException] = it => NotFoundException(Seq(it))
  implicit val clientFactory: ExceptionFactory[ClientException]     = it => ClientException(Seq(it))
  implicit val serverFactory: ExceptionFactory[ServerException]     = it => ServerException(Seq(it))

  val InternalServerError: RealWorldError = RealWorldError("error.internal")
  val InvalidToken: RealWorldError        = RealWorldError("token.invalid")
  val Unauthorized: RealWorldError        = RealWorldError("user.unauthorized")
  val InvalidCredentials: RealWorldError  = RealWorldError("user.invalid")

  def notFound[A: TypeTag](id: ModelId): RealWorldError =
    RealWorldError(typeOf[A].getClass.getSimpleName + ".notFound", id.value)

  implicit class RealWorldErrorOps(val realWorldError: RealWorldError) extends AnyVal {

    def ex[A](implicit factory: ExceptionFactory[A]): A = factory(realWorldError)
  }
}
