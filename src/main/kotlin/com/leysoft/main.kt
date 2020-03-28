package com.leysoft

import arrow.fx.IO
import arrow.fx.extensions.io.effect.effect
import arrow.fx.extensions.io.monad.flatMap
import com.leysoft.adapter.out.InMemoryPersonRepository
import com.leysoft.application.DefaultPersonService

fun main(args: Array<String>): Unit {
    InMemoryPersonRepository.make(IO.effect())
        .flatMap { DefaultPersonService.make(IO.effect(), it) }
        .flatMap { it.getAll() }
        .map { println(it) }
        .unsafeRunSync()
}