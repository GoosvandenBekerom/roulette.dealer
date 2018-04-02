package com.goosvandenbekerom.roulette.dealer

import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {
    companion object {
        val queueName = "roulette-dealer"
        val topicExchangeName = "roulette-dealer-exchange"
        val routingKey = "dealer.#"
    }

    @Bean
    fun queue() = Queue(queueName, true)

    @Bean
    fun exchange() = TopicExchange(topicExchangeName)

    @Bean
    fun binding(queue: Queue, topicExchange: TopicExchange) =
            BindingBuilder.bind(queue).to(exchange()).with(routingKey)

    @Bean
    fun container(cf: ConnectionFactory, adapter: MessageListenerAdapter) : SimpleMessageListenerContainer {
        val container = SimpleMessageListenerContainer()
        container.connectionFactory = cf
        container.setQueueNames(queueName)
        container.messageListener = adapter
        return container
    }

    @Bean
    fun listenerAdapter(receiver: MessageHandler) : MessageListenerAdapter =
            MessageListenerAdapter(receiver, "receiveMessage")
}