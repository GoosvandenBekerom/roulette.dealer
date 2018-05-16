package com.goosvandenbekerom.roulette.dealer

import com.goosvandenbekerom.roulette.core.Game
import com.goosvandenbekerom.roulette.core.Player
import com.goosvandenbekerom.roulette.exception.*
import org.springframework.stereotype.Component

@Component
class State {
    companion object {
        var counter: Long = 0
    }
    private val connectedPlayers = mutableMapOf<Long, Player>()

    fun connectPlayer(p: Player): Long {
        val id = ++counter
        connectedPlayers[id] = p
        return id
    }
    fun getPlayerById(id: Long): Player = connectedPlayers[id] ?: throw PlayerNotFoundException(id)
}