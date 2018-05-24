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
    companion object {
        private const val spinInterval: Long = 30000
        private const val updateInterval: Long = 5000
    }

    @Autowired lateinit var game: Game
    @Autowired lateinit var rabbit: RabbitTemplate
    @Autowired lateinit var updateExchange: FanoutExchange

    private var roundStartedTime = Date()

    override fun run(vararg args: String?) {
        // Open betting for the game when the application starts
        game.openBetting()
    }

    @Scheduled(fixedRate = spinInterval, initialDelay = spinInterval)
    fun playGameIfReady() {
        roundStartedTime = Date()

        if (!gameIsReady()) return

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

        game.openBetting()
        println("Broadcasting 'betting opened' update to connected players")
        val openBettingUpdate = UpdateBettingStatus.newBuilder()
        openBettingUpdate.status = true
        rabbit.convertAndSend(updateExchange.name, RabbitConfig.UPDATE_PLAYER_ROUTING_KEY, openBettingUpdate.build())
    }

//    @Scheduled(fixedRate = updateInterval)
//    fun updateClients() {
//        if (roundStartedTime.time < Date().time - 25000) return // The round has more then 5 seconds left
//        //TODO: if (!gameIsReady()) return
//
//        println("The current round will end in less then 5 seconds")
//        // TODO: send update to clients
//    }

    private fun gameIsReady(): Boolean = !game.bets.isEmpty()
}
