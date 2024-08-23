package com.nikileg.todo.domain.model

import cats.Show

import java.util.UUID

case class TodoList(id: UUID, items: IndexedSeq[TodoItem])

object TodoList {
  implicit def show: Show[TodoList] = Show.show(tl =>
    s"""
      |TodoList:
      | ${tl.items.mkString("\n")}
      |""".stripMargin)
}

