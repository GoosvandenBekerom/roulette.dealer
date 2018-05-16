package com.goosvandenbekerom.roulette.dealer

import com.goosvandenbekerom.roulette.core.Game
import com.goosvandenbekerom.roulette.core.Player
import com.goosvandenbekerom.roulette.exception.*
import org.springframework.stereotype.Component

@Component
class State {
    private val games = mutableSetOf<Game>()
    private val connectedPlayers = mutableMapOf<Long, Player>()

    fun createGame(minimumBet: Int) = Game(0, minimumBet)
    fun getGameById(id: Long): Game = games.find { g -> g.id == id } ?: throw GameNotFoundException(id)
    fun connectPlayer(p: Player)= connectedPlayers.put(p.hashCode().toLong(), p)
    fun getPlayerById(id: Long): Player = connectedPlayers[id] ?: throw PlayerNotFoundException(id)
}