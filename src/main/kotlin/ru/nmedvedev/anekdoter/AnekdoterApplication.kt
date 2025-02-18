package ru.nmedvedev.anekdoter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AnekdoterApplication

fun main(args: Array<String>) {
    runApplication<AnekdoterApplication>(*args)
}
