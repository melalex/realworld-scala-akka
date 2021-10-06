package com.melalex.realworld
package commons.errors.model

abstract class RealWorldException(cause: Throwable = null) extends Exception(cause)

case class SecurityException(errors: Seq[RealWorldError], cause: Throwable = null) extends RealWorldException(cause)
case class NotFoundException(errors: Seq[RealWorldError], cause: Throwable = null) extends RealWorldException(cause)
case class ClientException(errors: Seq[RealWorldError], cause: Throwable = null)   extends RealWorldException(cause)
case class ServerException(errors: Seq[RealWorldError], cause: Throwable = null)   extends RealWorldException(cause)
