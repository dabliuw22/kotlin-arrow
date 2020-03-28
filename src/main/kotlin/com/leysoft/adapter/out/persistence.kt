package com.leysoft.adapter.out

import arrow.Kind
import arrow.core.Option
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.handleError
import arrow.fx.typeclasses.Effect
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import com.leysoft.domain.Person
import com.leysoft.domain.PersonRepository
import java.lang.RuntimeException

class InMemoryPersonRepository<F> private constructor(
    private val A: Effect<F>) : PersonRepository<F>, Effect<F> by A {

    private val store = mutableMapOf("12345" to Person(id = "12345", name = "Test"))

    override fun findById(id: String): Kind<F, Person> = A.later { store[id] }
        .map { it ?: throw RuntimeException("Not found person: $id") }

    override fun findAll(): Kind<F, List<Person>> = A.later { store.values.toList() }

    override fun save(person: Person): Kind<F, Unit> = A.later { store[person.id] = person }
        .map { Unit }

    override fun delete(person: Person): Kind<F, Boolean> = A.later { store.remove(person.id, person) }
        .handleError { false }

    companion object {

        fun <F> build(A: Effect<F>): PersonRepository<F> = InMemoryPersonRepository(A)

        fun <F> make(A: Effect<F>): Kind<F, PersonRepository<F>> = A.later { build(A) }
    }
}