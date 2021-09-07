package com.melalex.realworld
package commons.db

trait DbInitRequired[DB[_]] {

  def init(): DB[Unit]
}
