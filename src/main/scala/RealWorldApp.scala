package com.melalex.realworld

import config.AppComponents

import akka.actor.Terminated
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding

import scala.concurrent.Future
import scala.util.{Failure, Success}

object RealWorldApp extends App with AppComponents {

  private def initDatabase(): Future[Unit] =
    dbBootstrap.init(initRequired.toSeq)

  private def startServer() =
    Http()
      .newServerAt(realWorldProperties.server.host, realWorldProperties.server.port)
      .bindFlow(routes)

  private def afterStart(binding: ServerBinding): Unit = {
    system.log.info("Server started on {}", binding.localAddress.toString)
  }

  private def onError(exception: Throwable): Future[Terminated] = {
    system.log.error("Failed to bind. Shutting down", exception)

    system.terminate()
  }

  initDatabase().flatMap(_ => startServer()).onComplete {
    case Success(binding) => afterStart(binding)
    case Failure(error)   => onError(error)
  }
}
