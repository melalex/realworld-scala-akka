package com.melalex.realworld
package commons

import java.time.{Clock, ZoneId, ZoneOffset}

trait CommonComponents {

  lazy val clock: Clock   = Clock.systemUTC()
  lazy val zoneId: ZoneId = ZoneOffset.UTC
}
