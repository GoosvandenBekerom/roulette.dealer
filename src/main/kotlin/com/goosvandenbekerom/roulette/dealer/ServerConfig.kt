package com.goosvandenbekerom.roulette.dealer

import com.goosvandenbekerom.roulette.core.Game
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServerConfig {
    @Bean
    fun game() = Game(1, 5)
}