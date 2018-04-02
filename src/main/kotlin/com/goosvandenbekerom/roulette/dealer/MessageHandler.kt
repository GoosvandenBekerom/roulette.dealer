package com.goosvandenbekerom.roulette.dealer

import org.springframework.stereotype.Component

@Component
class MessageHandler {
    fun receiveMessage(msg: String) {
        println("Received message: $msg")
    }
}