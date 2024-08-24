package com.nikileg.todo.domain

import cats.effect.Sync
import cats.effect.std.Console
import cats.syntax.all._
import com.nikileg.todo.domain.model.TodoList.ListId
import com.nikileg.todo.domain.model.{TodoItem, TodoList}
import doobie.Transactor
import doobie.implicits._
import doobie.postgres.implicits._
import org.typelevel.log4cats.Logger

import java.util.UUID

trait TodoRepository[F[_]] {
  def getById(id: UUID): F[Option[TodoList]]

  def remove(id: UUID): F[Boolean]

  def update(todo: TodoList): F[Boolean]

  def list(): F[Seq[ListId]]
}

object TodoRepository {
  private case class TodoDto(id: UUID, description: String, title: String) {
    def asModel(items: Seq[ItemDto]): TodoList = TodoList(id, items.map(_.asModel).toIndexedSeq)
  }

  private case class ItemDto(id: Int, listId: UUID, isChecked: Boolean, name: String, description: String) {
    def asModel: TodoItem = TodoItem(name, isChecked, description)
  }

  private object ItemDto {
    def fromString(s: String): ItemDto = {
      val Array(id, listId, isChecked, name, description) = s.split(',')
      ItemDto(id.toInt, UUID.fromString(listId), isChecked.toBoolean, name, description)
    }
  }

  def live[F[_] : Sync : Console : Logger](tx: Transactor[F]) = new TodoRepository[F] {
    override def getById(id: UUID): F[Option[TodoList]] = {
      val fr =
        sql"""
         select t.id, t.description, t.title from todolist.todos t
          where t.id = $id
         """
      val fr2 =
        sql"""
          select it.id, it.list_id, it.is_checked, it.name, it.description
           from todolist.items it
           where it.list_id = $id
           """
      (for {
        v1 <- fr.query[TodoDto].option
        v2 <- fr2.query[ItemDto].to[List]
      } yield (v1, v2))
        .transact(tx).map(pair => pair._1.map(_.asModel(pair._2))).handleErrorWith {
          case e => Logger[F].error(e)("Failed to get TodoList") *>
            Console[F].printStackTrace(e).as(None)
        }
    }

    override def remove(id: UUID): F[Boolean] = ???

    override def update(todo: TodoList): F[Boolean] = ???

    override def list(): F[Seq[UUID]] =
      sql"""
           select id from todolist.todos;
         """.query[UUID].to[List].transact(tx).map(_.toSeq)
  }
}
