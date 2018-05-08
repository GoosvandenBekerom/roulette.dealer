package com.goosvandenbekerom.roulette.dealer

import com.goosvandenbekerom.roulette.core.Player
import org.springframework.stereotype.Component

@Component
class State {
    val connectedPlayers = mutableSetOf<Player>()
}