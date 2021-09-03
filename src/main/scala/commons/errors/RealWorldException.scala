package com.melalex.realworld
package commons.errors

import commons.errors.model.RealWorldError

sealed trait RealWorldException { self =>
  Exception
}

case class SecurityException(errors: Seq[RealWorldError]) extends Exception with RealWorldException
case class NotFoundException(errors: Seq[RealWorldError]) extends Exception with RealWorldException
case class ClientException(errors: Seq[RealWorldError])   extends Exception with RealWorldException
case class ServerException(errors: Seq[RealWorldError])   extends Exception with RealWorldException
