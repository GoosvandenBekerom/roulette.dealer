package com.goosvandenbekerom.roulette.dealer

import com.google.protobuf.Message
import com.goosvandenbekerom.roulette.proto.RouletteProto.*
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DealerScheduledTasks : CommandLineRunner {
    companion object {
        private const val spinInterval: Long = 30000
    }

    @Autowired lateinit var state: State
    @Autowired lateinit var rabbit: RabbitTemplate

    override fun run(vararg args: String?) {
        // Open betting for the game when the application starts
        state.game.openBetting()
    }

    @Scheduled(fixedRate = spinInterval, initialDelay = spinInterval)
    fun playGameIfReady() {
        if (!gameIsReady()) return

        state.game.closeBetting()
        broadcastBetStatus()

        // Cache players that bet this round
        val betPlayers = state.game.bets.map { it.player }.toSet()

        val result = state.game.playAndReset()
        println("Broadcasting new result: $result")

        val resultUpdate = NewResult.newBuilder().setNumber(result.number).setColor(result.color.name)
        broadcast(resultUpdate.build())

        // Pay out
        betPlayers.forEach {
            val update = PlayerAmountUpdate.newBuilder().setPlayerId(state.getPlayerId(it)).setAmount(it.chipAmount)
            rabbit.convertAndSend(RabbitConfig.topicExchangeName, "player.${update.playerId}", update.build())
            println("Sent new amount update to betting player ${update.playerId}")
        }

        state.game.openBetting()
        broadcastBetStatus()
    }

    private fun gameIsReady(): Boolean = !state.game.bets.isEmpty()
    private fun broadcastBetStatus() {
        val update = UpdateBettingStatus.newBuilder().setStatus(state.game.bettingOpen)
        println("Broadcasting 'betting ${if (update.status) "opened" else "closed"}' update to connected players")
        broadcast(update.build())
    }

    private fun broadcast(message: Message) {
        rabbit.convertAndSend(RabbitConfig.fanoutExchangeName, RabbitConfig.UPDATE_PLAYER_ROUTING_KEY, message)
    }
}
