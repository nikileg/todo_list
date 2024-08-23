package com.nikileg.todo.domain.model

import cats.Show

case class TodoItem(name: String, isChecked: Boolean, description: String)

object TodoItem {
  implicit val show: Show[TodoItem] = Show.show(t =>
    s"""
      | [${if (t.isChecked) "+" else "-"}] ${t.name}: ${t.description}
      |""".stripMargin)
}
