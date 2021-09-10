package com.melalex.realworld
package commons

import java.time.Clock

trait CommonComponents {

  lazy val clock: Clock = Clock.systemUTC()
}
