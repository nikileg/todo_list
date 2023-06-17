package com.nikileg.todo.domain.model

import java.util.UUID

case class TodoList(id: UUID, items: IndexedSeq[TodoItem])

