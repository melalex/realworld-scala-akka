package com.melalex.realworld
package commons.web

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.directives.FutureDirectives
import akka.http.scaladsl.server.{Directives, Route}
import cats.data.EitherT

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait RealWorldDirectives { self: Directives with FutureDirectives =>

  def completeEitherT[A <: Throwable, B](response: => EitherT[Future, A, B]): Route = completeEither(response.value)

  def completeEither[A <: Throwable, B <: ToResponseMarshallable](response: => Future[Either[A, B]]): Route = onComplete(response) {
    case Success(either) =>
      either match {
        case Left(exception) => failWith(exception)
        case Right(value)    => complete(value)
      }
    case Failure(exception) => failWith(exception)
  }
}

object RealWorldDirectives extends RealWorldDirectives with Directives with FutureDirectives
