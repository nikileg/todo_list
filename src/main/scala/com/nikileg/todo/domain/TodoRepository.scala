package com.nikileg.todo.domain

import com.nikileg.todo.domain.model.TodoList

import java.util.UUID

trait TodoRepository[F[_]] {
  def getById(id: UUID): F[Option[TodoList]]
  def remove(id: UUID): F[Boolean]
  def update(todo: TodoList): F[Boolean]
}

//object TodoRepository {
//  def live[F[_]: Sync](tx: Transactor[F]) = new TodoRepository[F] {
//    override def getById(id: UUID): F[Option[TodoList]] =
//      sql"""
//
//         """.query[TodoList].option.transact(tx)
//
//    override def remove(id: UUID): F[Boolean] = ???
//
//    override def update(todo: TodoList): F[Boolean] = ???
//  }
//
//  def init[F[_]: Sync](tx: Transactor[F]) = {
//    sql"""
//          create table if not exists todolists (
//
//          )
//       """
//  }
//}
