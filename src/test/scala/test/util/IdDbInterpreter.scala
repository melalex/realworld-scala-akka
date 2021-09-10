package com.melalex.realworld
package test.util

import commons.db.DbInterpreter

import cats.Id
import cats.implicits.toTraverseOps

object IdDbInterpreter extends DbInterpreter[Id, Id] {

  override def execute[A](action: Id[A]): Id[A] = action

  override def executeTransitionally[A](action: Id[A]): Id[A] = action

  override def sequence[A](action: Seq[Id[A]]): Id[Seq[A]] = action.traverse(identity)
}
