package com.leysoft.domain

import java.util.UUID

data class Person(
    val id: String = UUID.randomUUID().toString(),
    val name: String
) {
    companion object
}