package com.melalex.realworld
package fixture

import commons.util.InstantProvider

import java.time.{Instant, LocalDateTime, ZoneOffset}

class FixedInstantProvider(now: Instant = FixedInstantProvider.Now) extends InstantProvider {

  override def provide(): Instant = now
}

object FixedInstantProvider {

  val Now: Instant = LocalDateTime.of(2021, 9, 8, 12, 0).toInstant(ZoneOffset.UTC)
}
