package ru.nmedvedev.anekdoter

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<AnekdoterApplication>().with(TestcontainersConfiguration::class).run(*args)
}
