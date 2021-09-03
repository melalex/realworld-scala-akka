package com.melalex.realworld
package commons.db

trait DbInterpreter[F[_], DB[_]] {

  def execute[A](action: DB[A]): F[A]

  def executeTransitionally[A](action: DB[A]): F[A]

  def sequence[A](action: Seq[DB[A]]): DB[Seq[A]]
}
