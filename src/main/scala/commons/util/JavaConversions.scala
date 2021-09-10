package com.melalex.realworld
package commons.util

import java.time.{Duration => JDuration}
import scala.concurrent.duration.FiniteDuration

trait JavaConversions {

  implicit class ScalaDurationOps(finiteDuration: FiniteDuration) {

    def asJava: JDuration = JDuration.ofSeconds(finiteDuration.toSeconds)
  }
}

object JavaConversions extends JavaConversions
