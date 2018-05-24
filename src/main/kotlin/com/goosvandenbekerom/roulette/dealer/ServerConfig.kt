package com.goosvandenbekerom.roulette.dealer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServerConfig {
    @Bean
    fun state() = State()
}