package com.leysoft.domain

import arrow.Kind

interface PersonRepository<F> {

    fun findById(id: String): Kind<F, Person>

    fun findAll(): Kind<F, List<Person>>

    fun save(person: Person): Kind<F, Unit>

    fun delete(person: Person): Kind<F, Boolean>
}