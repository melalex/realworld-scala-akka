package com.melalex.realworld
package config

import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext

trait AkkaComponents {

  implicit val system: ActorSystem        = ActorSystem("real-world")
  implicit val executor: ExecutionContext = system.dispatcher
}
