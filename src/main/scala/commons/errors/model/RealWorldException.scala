package com.melalex.realworld
package commons.errors.model

sealed trait RealWorldException { self: Exception =>

}

case class CredentialsException(errors: Seq[RealWorldError], cause: Throwable = null) extends Exception(cause) with RealWorldException
case class NotFoundException(errors: Seq[RealWorldError], cause: Throwable = null)    extends Exception(cause) with RealWorldException
case class ClientException(errors: Seq[RealWorldError], cause: Throwable = null)      extends Exception(cause) with RealWorldException
case class ServerException(errors: Seq[RealWorldError], cause: Throwable = null)      extends Exception(cause) with RealWorldException
