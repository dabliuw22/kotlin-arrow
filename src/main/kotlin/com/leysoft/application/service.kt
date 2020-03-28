package com.leysoft.application

import arrow.Kind
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.typeclasses.Effect
import com.leysoft.domain.Person
import com.leysoft.domain.PersonRepository

interface PersonService<F> {

    fun getById(id: String): Kind<F, Person>

    fun getAll(): Kind<F, List<Person>>

    fun create(person: Person): Kind<F, Unit>

    fun delete(person: Person): Kind<F, Boolean>
}

class DefaultPersonService<F> private constructor(
    private val A: Effect<F>,
    private val repository: PersonRepository<F>) : PersonService<F>, Effect<F> by A {

    override fun getById(id: String): Kind<F, Person> = repository.findById(id)

    override fun getAll(): Kind<F, List<Person>> = repository.findAll()

    override fun create(person: Person): Kind<F, Unit> = repository.save(person)

    override fun delete(person: Person): Kind<F, Boolean> = repository.delete(person)

    companion object {

        fun <F> build(A: Effect<F>, repository: PersonRepository<F>) : PersonService<F> = DefaultPersonService(A, repository)

        fun <F> make(A: Effect<F>, repository: PersonRepository<F>) : Kind<F, PersonService<F>> = A.later { build(A, repository) }
    }
}