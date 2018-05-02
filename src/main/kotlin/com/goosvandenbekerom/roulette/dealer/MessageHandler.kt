package com.goosvandenbekerom.roulette.dealer

import org.springframework.stereotype.Component
import com.goosvandenbekerom.roulette.proto.RouletteProto.*
import org.springframework.amqp.rabbit.annotation.RabbitListener

@Component
class MessageHandler {
    @RabbitListener(queues = [RabbitConfig.queueName], containerFactory = "listenerFactory")
    fun receiveMessage(msg: com.google.protobuf.Message) {
        when (msg) {
            is NewPlayerRequest -> {
                println("Received new player request: ${msg.name}")
                //create new player and reply with RouletteProto.NewPlayerResponse?
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