package com.leysoft.domain

import arrow.Kind
import arrow.core.Option

interface PersonRepository<F> {

    fun findById(id: String): Kind<F, Option<Person>>

    fun findAll(): Kind<F, List<Person>>

    fun save(person: Person): Kind<F, Unit>

    fun delete(person: Person): Kind<F, Boolean>
}