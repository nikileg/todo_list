package com.nikileg.todo.domain.model

import cats.Show
import com.nikileg.todo.domain.model.TodoList.ListId
import cats.syntax.all._

import java.util.UUID


case class TodoList(id: ListId, items: IndexedSeq[TodoItem])

object TodoList {
  type ListId = UUID

  implicit def show: Show[TodoList] = Show.show(tl =>
    s"""
      |TodoList:
      | ${tl.items.map(_.show).mkString("\n")}
      |""".stripMargin)
}

