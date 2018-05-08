package com.goosvandenbekerom.roulette.dealer

import com.goosvandenbekerom.roulette.core.Player
import com.goosvandenbekerom.roulette.proto.RouletteProto
import com.goosvandenbekerom.roulette.proto.RouletteProto.*
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MessageHandler {
    @Autowired lateinit var state: State
    @Autowired lateinit var rabbit: RabbitTemplate

    @RabbitListener(queues = [RabbitConfig.queueName], containerFactory = "listenerFactory")
    fun receiveRequest(request: Request) {
        when (request.message) {
            is NewPlayerRequest -> {
                println("Received new player request: username = ${request.message.name}")
                val player = Player(request.message.name, "")
                val response = RouletteProto.NewPlayerResponse.newBuilder()
                response.id = player.hashCode().toLong()
                state.connectedPlayers.add(player)
                println("Responding with id ${response.id}")
                rabbit.convertAndSend(request.replyTo, RouletteMessage(response.build(), request.correlationKey))
            }
            is BuyInRequest -> {
                TODO("get player, add requested amount to chips, reply with RouletteProto.PlayerAmountUpdate?")
            }
            is BetRequest -> {
                TODO("get game, get player, bet amount on bet type on game for player, reply with RouletteProto.PlayerAmountUpdate?")
            }
        }
    }
}