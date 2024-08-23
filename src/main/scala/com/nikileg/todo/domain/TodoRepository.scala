package com.nikileg.todo.domain

import cats.effect.Sync
import com.nikileg.todo.domain.model.{TodoItem, TodoList}
import doobie.Transactor
import doobie.implicits._
import cats.implicits._
import doobie.postgres._
import doobie.postgres.implicits._

import java.util.UUID

trait TodoRepository[F[_]] {
  def getById(id: UUID): F[Option[TodoList]]
  def remove(id: UUID): F[Boolean]
  def update(todo: TodoList): F[Boolean]
}

object TodoRepository {
  private case class TodoDto(id: UUID, description: String, title: String/*, items: List[ItemDto]*/) {
    def asModel: TodoList = TodoList(id, /*items.map(_.asModel).toIndexedSeq*/ IndexedSeq.empty)
  }
  private case class ItemDto(description: String, isChecked: Boolean, name: String) {
    def asModel: TodoItem = TodoItem(name, isChecked, description)
  }

  def live[F[_]: Sync](tx: Transactor[F]) = new TodoRepository[F] {
    override def getById(id: UUID): F[Option[TodoList]] =
      sql"""
         select t.id, t.description, t.title from todolist.todos t
          join todolist.items it on t.id = it.list_id
          where id = $id
         """.query[TodoDto].to[List].map(_.headOption).transact(tx).map(_.map(_.asModel))

    override def remove(id: UUID): F[Boolean] = ???

    override def update(todo: TodoList): F[Boolean] = ???
  }
}
