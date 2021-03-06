package com.goosvandenbekerom.roulette.dealer

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {
    companion object {
        const val queueName = "roulette-dealer"
        const val topicExchangeName = "roulette-dealer-exchange"
        const val fanoutExchangeName = "roulette-update-exchange"
        const val routingKey = "dealer"
        const val UPDATE_PLAYER_ROUTING_KEY = "game.update"
    }

    @Bean
    fun queue() = Queue(queueName, true)

    @Bean
    fun topicExchange() = TopicExchange(topicExchangeName)

    @Bean
    fun fanoutExchange() = FanoutExchange(fanoutExchangeName)

    @Bean
    fun binding(queue: Queue, topicExchange: TopicExchange)
            = BindingBuilder.bind(queue).to(topicExchange()).with(routingKey)!!

    @Bean
    fun protoMessageConverter() = ProtoMessageConverter()

    @Bean
    fun rabbitTemplate(cf: ConnectionFactory): RabbitTemplate {
        val template = RabbitTemplate(cf)
        template.messageConverter = protoMessageConverter()
        return template
    }

    @Bean
    fun listenerFactory(cf: ConnectionFactory, configurer: SimpleRabbitListenerContainerFactoryConfigurer): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        configurer.configure(factory, cf)
        factory.setMessageConverter(protoMessageConverter())
        return factory
    }
}