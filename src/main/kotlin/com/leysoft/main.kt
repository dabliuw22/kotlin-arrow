package com.leysoft

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.Ref
import arrow.fx.extensions.io.effect.effect
import arrow.fx.extensions.io.monadDefer.monadDefer
import arrow.fx.fix
import arrow.fx.reactor.ForMonoK
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.extensions.monok.effect.effect
import arrow.fx.reactor.extensions.monok.monadDefer.monadDefer
import arrow.fx.reactor.fix
import arrow.fx.rx2.ForObservableK
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.extensions.observablek.effect.effect
import arrow.fx.rx2.extensions.observablek.monadDefer.monadDefer
import arrow.fx.rx2.fix
import com.leysoft.adapter.out.InMemoryPersonRepository
import com.leysoft.application.DefaultPersonService
import com.leysoft.domain.Person

fun main() {

    val store = mapOf(
        "12345" to Person(id = "12345", name = "Test")
    )

    // Rx
    val refRx: Ref<ForObservableK, Map<String, Person>> = Ref(ObservableK.monadDefer(), store)
        .fix().observable.blockingFirst()
    val rxRepository = InMemoryPersonRepository.build(ObservableK.effect(), refRx)
    val rxService = DefaultPersonService.build(ObservableK.effect(), rxRepository)
    rxService.getAll().fix().observable.subscribe(
        { println("[Rx] All: $it") },
        { println("[Rx] Error: $it") }
    )
    rxService.getById("12345").fix().observable.subscribe(
        { println("[Rx] ById: $it") },
        { println("[Rx] Error: $it") }
    )
    rxService.create(Person(name = "New Person")).fix().observable.subscribe()
    rxService.getAll().fix().observable.subscribe(
        { println("[Rx] All: $it") },
        { println("[Rx] Error: $it") }
    )

    // Reactor Project
    val refReactor : Ref<ForMonoK, Map<String, Person>> = Ref(MonoK.monadDefer(), store)
        .fix().mono.block()
    val reactorRepository = InMemoryPersonRepository.build(MonoK.effect(), refReactor)
    val reactorService = DefaultPersonService.build(MonoK.effect(), reactorRepository)
    reactorService.getAll().fix().mono.subscribe(
        { println("[Reactor] All: $it") },
        { println("[Reactor] Error: $it") }
    )

    // Arrow IO
    val refIO: IO<Ref<ForIO, Map<String, Person>>> = Ref(IO.monadDefer(), store).fix()
    refIO.flatMap { InMemoryPersonRepository.make(IO.effect(), it) }
        .flatMap { DefaultPersonService.make(IO.effect(), it) }
        .flatMap { it.getAll() }
        .unsafeRunAsync {
            when (it) {
                is Right -> println("[IO] All: ${it.b}")
                is Left  -> println("[IO] Error: ${it.a}")
            }
        }
}