package com.melalex.realworld
package commons.db

import cats.Monad
import cats.implicits._

class DbBootstrap[F[_]: Monad, DB[_]](dbInterpreter: DbInterpreter[F, DB]) {

  def init(initRequired: DbInitRequired[DB]): F[Unit] = init(Seq(initRequired))

  def init(initRequired: Seq[DbInitRequired[DB]]): F[Unit] =
    dbInterpreter
      .executeTransitionally(
        dbInterpreter.sequence(
          initRequired.map(_.init())
        )
      )
      .map(_ => ())

  def drop(droppable: Droppable[DB]): F[Unit] = drop(Seq(droppable))

  def drop(droppable: Seq[Droppable[DB]]): F[Unit] =
    dbInterpreter
      .executeTransitionally(
        dbInterpreter.sequence(
          droppable.map(_.drop())
        )
      )
      .map(_ => ())
}
