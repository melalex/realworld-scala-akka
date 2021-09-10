package com.melalex.realworld
package commons.db

trait Droppable[DB[_]] {

  def drop(): DB[Unit]
}
