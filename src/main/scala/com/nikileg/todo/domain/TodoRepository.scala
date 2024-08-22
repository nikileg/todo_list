package com.nikileg.todo.domain

import cats.Monad
import cats.effect.Sync
import cats.effect.std.Console
import com.nikileg.todo.domain.model.{TodoItem, TodoList}
import doobie.Transactor
import doobie.implicits._
import cats.syntax.all._
import com.nikileg.todo.domain.model.TodoList.ListId
import doobie.enumerated.JdbcType
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.util.Get
import doobie.util.invariant.InvalidObjectMapping
import org.postgresql.util.PGobject
import org.typelevel.log4cats.Logger

import java.util.UUID

trait TodoRepository[F[_]] {
  def getById(id: UUID): F[Option[TodoList]]

  def remove(id: UUID): F[Boolean]

  def update(todo: TodoList): F[Boolean]

  def list(): F[Seq[ListId]]
}

object TodoRepository {
  private case class TodoDto(id: UUID, description: String, title: String, items: List[ItemDto]) {
    def asModel: TodoList = TodoList(id, items.map(_.asModel).toIndexedSeq)
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

//  private implicit val itemDtoGet: Get[ItemDto] = Get[String].map(ItemDto.fromString)
  private implicit val itemDtoArrayGet: Get[Array[ItemDto]] = Get[String].map(
    _.stripPrefix("{").stripSuffix("}").split(",").map(
      _.stripPrefix("(").stripSuffix(")")
    ).map(ItemDto.fromString)
  )

  def live[F[_] : Sync : Console : Logger](tx: Transactor[F]) = new TodoRepository[F] {
    override def getById(id: UUID): F[Option[TodoList]] = {
      val fr =
        sql"""
         select t.id, t.description, t.title, array_agg(it.name)::text from todolist.todos t
          left join todolist.items it on t.id = it.list_id
          where t.id = $id
          group by t.id, t.description, t.title
         """
      fr.query[TodoDto].to[List].map(_.headOption).transact(tx).map(_.map(_.asModel)).handleErrorWith {
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
