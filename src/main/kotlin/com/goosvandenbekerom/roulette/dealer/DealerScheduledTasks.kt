package com.goosvandenbekerom.roulette.dealer

import com.goosvandenbekerom.roulette.core.Game
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.scheduling.annotation.Scheduled
import java.util.*

@Component
class DealerScheduledTasks : CommandLineRunner {
    @Autowired lateinit var game: Game
    @Autowired lateinit var rabbit: RabbitTemplate

    override fun run(vararg args: String?) {
        game.openBetting()
    }

    @Scheduled(fixedRate = 30000)
    fun playGameIfReady() {
        if (game.bets.isEmpty()) return

        // TODO: send betting closed update to exchange

        val result = game.playAndReset()
        // TODO: send result to exchange

        // TODO: send updated chip amount to each connected player

        println("New roulette result sent - $result")

        game.openBetting()
        // TODO: send betting opened update to exchange
    }
}
