package com.goosvandenbekerom.roulette.dealer

import com.google.protobuf.GeneratedMessageV3
import com.goosvandenbekerom.roulette.core.BetType
import com.goosvandenbekerom.roulette.core.Player
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
            is NewPlayerRequest -> handleNewPlayerRequest(request)
            is BuyInRequest -> handleBuyInRequest(request)
            is BetRequest -> handleBetRequest(request)
        }
    }

    private fun handleNewPlayerRequest(request: Request) {
        request.message as NewPlayerRequest
        println("Received new player request: username = ${request.message.name}")
        val player = Player(request.message.name, "")
        val response = NewPlayerResponse.newBuilder()
        response.id = player.hashCode().toLong()
        state.connectPlayer(player)
        println("Responding with id ${response.id}")
        reply(request, response.build())
    }

    private fun handleBuyInRequest(request: Request) {
        request.message as BuyInRequest
        println("Received buyin request: player id = ${request.message.playerId} - amount = ${request.message.amount}")
        val player = state.getPlayerById(request.message.playerId)
        player.addChips(request.message.amount)
        val response = PlayerAmountUpdate.newBuilder()
        response.playerId = player.hashCode().toLong()
        response.amount = player.chipAmount
        println("responding with player amount update")
        reply(request, response.build())
    }

    private fun handleBetRequest(request: Request) {
        request.message as BetRequest
        //val game = state.getGameById(request.message.gameId)
        val player = state.getPlayerById(request.message.playerId)
        //game.placeBet(player, request.message.amount, protoToBetType(request.message.type, *request.message.numberList.toIntArray()))
        val response = PlayerAmountUpdate.newBuilder()
        response.playerId = player.hashCode().toLong()
        response.amount = player.chipAmount
        println("responding with player amount update")
        reply(request, response.build())
    }

    private fun reply(request: Request, response: GeneratedMessageV3) {
        rabbit.convertAndSend(request.replyTo, RouletteMessage(response, request.correlationKey))
    }

    private fun protoToBetType(type: BetRequest.BetType, vararg numbers: Int): BetType {
        return when(type) {
            BetRequest.BetType.ODD -> BetType.Odd()
            BetRequest.BetType.EVEN -> BetType.Even()
            BetRequest.BetType.RED -> BetType.Red()
            BetRequest.BetType.BLACK -> BetType.Black()
            BetRequest.BetType.FIRST_HALf -> BetType.FirstHalf()
            BetRequest.BetType.SECOND_HALF -> BetType.SecondHalf()
            BetRequest.BetType.FIRST_DOZEN -> BetType.FirstDozen()
            BetRequest.BetType.SECOND_DOZEN -> BetType.SecondDozen()
            BetRequest.BetType.THIRD_DOZEN -> BetType.ThirdDozen()
            BetRequest.BetType.FIRST_COLUMN -> BetType.FirstColumn()
            BetRequest.BetType.SECOND_COLUMN -> BetType.SecondColumn()
            BetRequest.BetType.THIRD_COLUMN -> BetType.ThirdColumn()
            BetRequest.BetType.NUMBER -> BetType.Number(numbers.first())
            BetRequest.BetType.TWO_NUMBER -> BetType.TwoNumber(*numbers)
            BetRequest.BetType.THREE_NUMBER -> BetType.ThreeNumber(*numbers)
            BetRequest.BetType.FOUR_NUMBER -> BetType.FourNumber(*numbers)
            BetRequest.BetType.FIVE_NUMBER -> BetType.FiveNumber(*numbers)
            BetRequest.BetType.SIX_NUMBER -> BetType.SixNumber(*numbers)
            else -> throw Exception("Unsupported BetType received")
        }
    }
}