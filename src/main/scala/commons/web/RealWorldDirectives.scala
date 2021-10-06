package com.melalex.realworld
package commons.web

import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.directives.FutureDirectives
import akka.http.scaladsl.server.{Directives, Route}
import cats.data.EitherT

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait RealWorldDirectives { self: Directives with FutureDirectives =>

  def completeUnit(response: => Future[Unit]): Route = onComplete(response) {
    case Success(_)         => complete(StatusCodes.NoContent)
    case Failure(exception) => failWith(exception)
  }

  def completeEitherT[A <: Throwable, B](response: => EitherT[Future, A, B])(implicit
      m: ToResponseMarshaller[B]
  ): Route = completeEither(response.value)

  def completeUnitEitherT[A <: Throwable](response: => EitherT[Future, A, Unit]): Route =
    completeUnitEither(response.value)

  def completeEither[A <: Throwable, B](response: => Future[Either[A, B]])(implicit m: ToResponseMarshaller[B]): Route =
    onComplete(response) {
      case Success(either) => either match {
          case Left(exception) => failWith(exception)
          case Right(value)    => complete(value)
        }
      case Failure(exception) => failWith(exception)
    }

  def completeUnitEither[A <: Throwable](response: => Future[Either[A, Unit]]): Route = onComplete(response) {
    case Success(either) => either match {
        case Left(exception) => failWith(exception)
        case Right(_)        => complete(StatusCodes.NoContent)
      }
    case Failure(exception) => failWith(exception)
  }
}

object RealWorldDirectives extends RealWorldDirectives with Directives with FutureDirectives
