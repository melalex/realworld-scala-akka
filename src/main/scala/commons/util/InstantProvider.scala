package com.melalex.realworld
package commons.util

import java.time.Instant

trait InstantProvider {

  def provide(): Instant
}
