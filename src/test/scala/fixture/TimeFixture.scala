package com.melalex.realworld
package fixture

import java.time.{Clock, Instant}

trait TimeFixture {

  val Now: Instant = Instant.now()
}
