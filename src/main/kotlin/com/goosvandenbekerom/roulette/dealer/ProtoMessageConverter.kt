package com.goosvandenbekerom.roulette.dealer

import com.google.protobuf.GeneratedMessageV3
import com.goosvandenbekerom.roulette.domain.Request
import com.goosvandenbekerom.roulette.domain.RouletteMessage
import com.goosvandenbekerom.roulette.exception.UnsupportedMessageException
import com.goosvandenbekerom.roulette.proto.RouletteProto.*
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageDeliveryMode
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.support.converter.AbstractMessageConverter
import org.springframework.http.MediaType
import org.springframework.stereotype.Component

@Component
class ProtoMessageConverter : AbstractMessageConverter() {
    override fun createMessage(o: Any, properties: MessageProperties): Message {
        val obj = if (o is GeneratedMessageV3) RouletteMessage(o) else o as RouletteMessage

        val bytes = obj.message.toByteArray()
        properties.contentLength = bytes.size.toLong()
        properties.contentType = ProtoMessage.MEDIA_TYPE
        properties.setHeader(ProtoMessage.TYPE_KEY, obj.message::class.java.simpleName)
        properties.deliveryMode = MessageDeliveryMode.NON_PERSISTENT

        if (!obj.correlationId.isEmpty())
            properties.correlationId = obj.correlationId

        return Message(bytes, properties)
    }

    override fun fromMessage(message: Message): Request {
        val replyTo = message.messageProperties.replyTo
        val correlation = message.messageProperties.correlationId
        return when(message.messageProperties.headers[ProtoMessage.TYPE_KEY]) {
            NewPlayerRequest::class.java.simpleName -> Request(NewPlayerRequest.parseFrom(message.body), replyTo, correlation)
            BuyInRequest::class.java.simpleName -> Request(BuyInRequest.parseFrom(message.body), replyTo, correlation)
            BetRequest::class.java.simpleName -> Request(BetRequest.parseFrom(message.body), replyTo, correlation)
            else -> throw UnsupportedMessageException(message)
        }
    }

    object ProtoMessage {
        const val TYPE_KEY = "type"
        const val MEDIA_TYPE = MediaType.APPLICATION_OCTET_STREAM_VALUE
    }
}