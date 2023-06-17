package com.nikileg.todo.domain

import com.nikileg.todo.domain.model.TodoList

import java.util.UUID

trait TodoRepository[F[_]] {
  def getById(id: UUID): F[Option[TodoList]]
  def remove(id: UUID): F[Boolean]
  def update(todo: TodoList): F[Boolean]
}
