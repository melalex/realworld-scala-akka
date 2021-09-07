package com.melalex.realworld
package commons

import commons.util.InstantProvider
import commons.util.impl.ClockInstantProvider

import com.softwaremill.macwire.wire

import java.time.Clock

trait CommonComponents {

  lazy val clock: Clock                     = Clock.systemDefaultZone()
  lazy val instantProvider: InstantProvider = wire[ClockInstantProvider]
}
