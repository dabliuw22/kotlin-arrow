package com.leysoft.application

import arrow.Kind
import arrow.fx.ForIO
import arrow.fx.IO
import com.leysoft.domain.Person
import com.leysoft.domain.PersonRepository

interface PersonService<F> {

    fun getById(id: String): Kind<F, Person>

    fun getAll(): Kind<F, List<Person>>

    fun create(person: Person): Kind<F, Unit>

    fun delete(person: Person): Kind<F, Boolean>
}

class DefaultPersonService<F> private constructor(private val repository: PersonRepository<ForIO>) : PersonService<ForIO> {

    override fun getById(id: String): Kind<ForIO, Person> = repository.findById(id)

    override fun getAll(): Kind<ForIO, List<Person>> = repository.findAll()

    override fun create(person: Person): Kind<ForIO, Unit> = repository.save(person)

    override fun delete(person: Person): Kind<ForIO, Boolean> = repository.delete(person)

    companion object {

        fun make(repository: PersonRepository<ForIO>) : IO<PersonService<ForIO>> = IO { DefaultPersonService<ForIO>(repository) }
    }
}