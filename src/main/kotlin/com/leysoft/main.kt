package com.leysoft

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.fx.IO
import arrow.fx.extensions.fx
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
import java.util.UUID

fun main() {
    val initId = UUID.randomUUID().toString()
    val store = mapOf(
        initId to Person(id = initId, name = "Test")
    )

    io(store)
    rx(store, initId)
    reactor(store)
}

fun io(store: Map<String, Person>) {
    IO.fx {
        val refIO = Ref(IO.monadDefer(), store).fix().bind()
        val repository = !(InMemoryPersonRepository.make(IO.effect(), refIO))
        val service = !(DefaultPersonService.make(IO.effect(), repository))
        !(service.getAll())
    }.unsafeRunAsync {
        when (it) {
            is Right -> println("[IO] All: ${it.b}")
            is Left  -> println("[IO] Error: ${it.a}")
        }
    }
}

fun rx(store: Map<String, Person>, id: String) {
    val refRx: Ref<ForObservableK, Map<String, Person>> = Ref(ObservableK.monadDefer(), store)
        .fix().observable.blockingFirst()
    val rxRepository = InMemoryPersonRepository.build(ObservableK.effect(), refRx)
    val rxService = DefaultPersonService.build(ObservableK.effect(), rxRepository)
    rxService.getAll().fix().observable.subscribe(
        { println("[Rx] All: $it") },
        { println("[Rx] Error: $it") }
    )
    rxService.getById(id).fix().observable.subscribe(
        { println("[Rx] ById: $it") },
        { println("[Rx] Error: $it") }
    )
    rxService.create(Person(name = "New Person")).fix().observable.subscribe()
    rxService.getAll().fix().observable.subscribe(
        { println("[Rx] All: $it") },
        { println("[Rx] Error: $it") }
    )
}

fun reactor(store: Map<String, Person>) {
    val refReactor : Ref<ForMonoK, Map<String, Person>> = Ref(MonoK.monadDefer(), store)
        .fix().mono.block()
    val reactorRepository = InMemoryPersonRepository.build(MonoK.effect(), refReactor)
    val reactorService = DefaultPersonService.build(MonoK.effect(), reactorRepository)
    reactorService.getAll().fix().mono.subscribe(
        { println("[Reactor] All: $it") },
        { println("[Reactor] Error: $it") }
    )
}