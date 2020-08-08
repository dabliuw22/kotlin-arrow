package com.leysoft.adapter.out

import arrow.Kind
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.fx.Ref
import arrow.fx.typeclasses.Effect
import com.leysoft.domain.Person
import com.leysoft.domain.PersonRepository
import java.lang.RuntimeException

class InMemoryPersonRepository<F> private constructor(
    private val Q: Effect<F>,
    private val ref: Ref<F, Map<String, Person>>) : PersonRepository<F>, Effect<F> by Q {

    override fun findById(id: String): Kind<F, Person> = ref.get()
        .map { Option.fromNullable(it[id]) }
        .flatMap {
            when(it) {
                is Some -> just(it.t)
                else    -> raiseError(RuntimeException("Not found person: $id"))
            }
        }

    override fun findAll(): Kind<F, List<Person>> = ref.get()
        .map { it.values.toList() }

    override fun save(person: Person): Kind<F, Unit> = ref.get()
        .map { Option.fromNullable(it[person.id]) }
        .flatMap { result ->
            when(result) {
                is None -> ref.update { it.plus(Pair(person.id, person)) }
                else    -> raiseError(RuntimeException("Not save person: $person"))
            }
        }

    override fun delete(person: Person): Kind<F, Boolean> = ref.get()
        .map { Option.fromNullable(it[person.id]) }
        .flatMap {
            it.fold(
                { ref.update { store -> store.minus(person.id) }.map { true } },
                { just(false) }
            )
        }.handleError { false }

    companion object {

        fun <F> build(Q: Effect<F>, ref: Ref<F, Map<String, Person>>): PersonRepository<F> =
            InMemoryPersonRepository(Q, ref)

        fun <F> make(Q: Effect<F>, ref: Ref<F, Map<String, Person>>): Kind<F, PersonRepository<F>> =
            Q.later { build(Q, ref) }

    }
}