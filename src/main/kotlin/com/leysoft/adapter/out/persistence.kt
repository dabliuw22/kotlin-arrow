package com.leysoft.adapter.out

import arrow.Kind
import arrow.core.Option
import arrow.fx.typeclasses.Effect
import com.leysoft.domain.Person
import com.leysoft.domain.PersonRepository
import java.lang.RuntimeException

class InMemoryPersonRepository<F> private constructor(
    private val Q: Effect<F>) : PersonRepository<F>, Effect<F> by Q {

    private var store = mutableMapOf<String, Person>(
        "12345" to Person(id = "12345", name = "Test")
    )

    override fun findById(id: String): Kind<F, Person> = Option.fromNullable(store[id]).fold(
        { raiseError(RuntimeException("Not found person: $id")) },
        { just(it) }
    )

    override fun findAll(): Kind<F, List<Person>> = later { store.values.toList() }

    override fun save(person: Person): Kind<F, Unit> = Option.fromNullable(store.put(person.id, person))
        .fold({ just(Unit) }, { just(Unit) })

    override fun delete(person: Person): Kind<F, Boolean> = later { store.remove(person.id, person) }
        .handleError { false }

    companion object {

        fun <F> build(Q: Effect<F>): PersonRepository<F> = InMemoryPersonRepository(Q)

        fun <F> make(Q: Effect<F>): Kind<F, PersonRepository<F>> = Q.later { build(Q) }
    }
}