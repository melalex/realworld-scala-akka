package com.melalex.realworld
package test.util

import java.time.{Clock, Instant, LocalDateTime, ZoneOffset}

trait TimeSupport {

  protected val now: Instant = TimeSupport.Now
  protected val clock: Clock = TimeSupport.GlobalClock
}

object TimeSupport {

  val Now: Instant       = LocalDateTime.of(2021, 9, 8, 12, 0).toInstant(ZoneOffset.UTC)
  val GlobalClock: Clock = Clock.fixed(Now, ZoneOffset.UTC)
}
