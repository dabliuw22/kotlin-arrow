package com.leysoft

import arrow.Kind
import arrow.fx.typeclasses.Async
import com.leysoft.adapter.out.InMemoryPersonRepository
import com.leysoft.application.DefaultPersonService

interface HelloService<F> {

    fun hello(name: String): Kind<F, Unit>
}


class DefaultHelloService<F> private constructor(private val async: Async<F>): HelloService<F> {

    override fun hello(name: String): Kind<F, Unit> = async.later { println("Hello $name") }

    companion object {

        fun <F> make(async: Async<F>): Kind<F, HelloService<F>> = async.later { DefaultHelloService(async) }
    }
}

fun main() {
    InMemoryPersonRepository.make()
        .flatMap { DefaultPersonService.make(it) }
        .flatMap { it.getAll() }
        .map { println(it) }
        .unsafeRunSync()
}