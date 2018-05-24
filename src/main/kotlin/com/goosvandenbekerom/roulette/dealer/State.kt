package com.goosvandenbekerom.roulette.dealer

import com.goosvandenbekerom.roulette.core.Game
import com.goosvandenbekerom.roulette.core.Player
import com.goosvandenbekerom.roulette.exception.PlayerNotFoundException

class State {
    companion object {
        var counter: Long = 0
    }
    private val connectedPlayers = mutableMapOf<Long, Player>()
    val game = Game(1, 5)

    fun connectPlayer(p: Player): Long {
        val id = ++counter
        connectedPlayers[id] = p
        return id
    }
    fun getPlayerById(id: Long): Player = connectedPlayers[id] ?: throw PlayerNotFoundException(id)
    fun getPlayerId(p: Player) = connectedPlayers.filter { it.value == p }.entries.first().key
}