package com.goosvandenbekerom.roulette.dealer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class DealerApplication

fun main(args: Array<String>) {
    runApplication<DealerApplication>(*args)
}