package com.melalex.realworld
package commons.util.impl

import commons.util.InstantProvider

import java.time.{Clock, Instant}

class ClockInstantProvider(clock: Clock) extends InstantProvider {

  override def provide(): Instant = clock.instant()
}
