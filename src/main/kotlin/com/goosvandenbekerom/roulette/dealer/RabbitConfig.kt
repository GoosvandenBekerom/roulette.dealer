package com.goosvandenbekerom.roulette.dealer

import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
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
        const val routingKey = "dealer"
    }

    @Bean
    fun queue() = Queue(queueName, true)

    @Bean
    fun exchange() = TopicExchange(topicExchangeName)

    @Bean
    fun binding(queue: Queue, topicExchange: TopicExchange)
            = BindingBuilder.bind(queue).to(exchange()).with(routingKey)!!

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