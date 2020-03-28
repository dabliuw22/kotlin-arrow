package com.leysoft.adapter.out

import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.handleError
import com.leysoft.domain.Person
import com.leysoft.domain.PersonRepository
import java.lang.RuntimeException

class InMemoryPersonRepository private constructor() : PersonRepository<ForIO> {

    private val store = mutableMapOf("12345" to Person(id = "12345", name = "Test"))

    override fun findById(id: String): IO<Person> = IO { store[id] }
        .map { it ?: throw RuntimeException("Not found person: $id") }

    override fun findAll(): IO<List<Person>> = IO { store.values.toList() }

    override fun save(person: Person): IO<Unit> = IO { store[person.id] = person }
        .map { Unit }

    override fun delete(person: Person): IO<Boolean> = IO { store.remove(person.id, person) }
        .handleError { false }

    companion object {

        fun make(): IO<PersonRepository<ForIO>> = IO { InMemoryPersonRepository() }
    }
}