package com.leysoft

import arrow.fx.IO
import arrow.fx.extensions.io.effect.effect
import arrow.fx.extensions.io.monad.flatMap
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.extensions.monok.effect.effect
import arrow.fx.reactor.fix
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.extensions.observablek.effect.effect
import arrow.fx.rx2.fix
import com.leysoft.adapter.out.InMemoryPersonRepository
import com.leysoft.application.DefaultPersonService
import com.leysoft.domain.Person

fun main(args: Array<String>): Unit {

    // Rx
    val rxRepository = InMemoryPersonRepository.build(ObservableK.effect())
    val rxService = DefaultPersonService.build(ObservableK.effect(), rxRepository)
    rxService.getAll().fix().observable.subscribe(::println)
    rxService.getById("12345").fix().observable.subscribe(::println)
    rxService.create(Person(name = "New Person")).fix().observable.subscribe()
    rxService.getAll().fix().observable.subscribe(::println)

    // Reactor Project
    val reactorRepository = InMemoryPersonRepository.build(MonoK.effect())
    val reactorService = DefaultPersonService.build(MonoK.effect(), reactorRepository)
    reactorService.getAll().fix().mono.subscribe(::println)

    // Arrow IO
    InMemoryPersonRepository.make(IO.effect())
        .flatMap { DefaultPersonService.make(IO.effect(), it) }
        .flatMap { it.getAll() }
        .map { println(it) }
        .unsafeRunSync()
}