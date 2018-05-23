package com.goosvandenbekerom.roulette.dealer

import com.goosvandenbekerom.roulette.core.Game
import com.goosvandenbekerom.roulette.proto.RouletteProto
import com.goosvandenbekerom.roulette.proto.RouletteProto.*
import org.springframework.amqp.core.FanoutExchange
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
    @Autowired lateinit var updateExchange: FanoutExchange

    override fun run(vararg args: String?) {
        game.openBetting()
    }

    @Scheduled(fixedRate = 30000)
    fun playGameIfReady() {
        if (game.bets.isEmpty()) return

        game.closeBetting()
        println("Broadcasting 'betting closed' update to connected players")
        val closeBettingUpdate = UpdateBettingStatus.newBuilder()
        closeBettingUpdate.status = false
        rabbit.convertAndSend(updateExchange.name, RabbitConfig.UPDATE_PLAYER_ROUTING_KEY, closeBettingUpdate.build())

        val result = game.playAndReset()
        println("Broadcasting new result: $result")

        val resultUpdate = NewResult.newBuilder()
        resultUpdate.number = result.number
        resultUpdate.color = result.color.name
        rabbit.convertAndSend(updateExchange.name, RabbitConfig.UPDATE_PLAYER_ROUTING_KEY, resultUpdate.build())

        // TODO: send updated chip amount to each connected player
        // TODO: test this funtion!!!

        game.openBetting()
        println("Broadcasting 'betting opened' update to connected players")
        val openBettingUpdate = UpdateBettingStatus.newBuilder()
        closeBettingUpdate.status = true
        rabbit.convertAndSend(updateExchange.name, RabbitConfig.UPDATE_PLAYER_ROUTING_KEY, openBettingUpdate.build())
    }
}
