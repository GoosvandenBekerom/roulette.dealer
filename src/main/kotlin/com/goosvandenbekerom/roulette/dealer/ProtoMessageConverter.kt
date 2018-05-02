package com.goosvandenbekerom.roulette.dealer

import com.google.protobuf.GeneratedMessageV3
import com.goosvandenbekerom.roulette.exception.UnsupportedMessageException
import com.goosvandenbekerom.roulette.proto.RouletteProto.*
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.support.converter.AbstractMessageConverter
import org.springframework.http.MediaType
import org.springframework.stereotype.Component

@Component
class ProtoMessageConverter : AbstractMessageConverter() {
    override fun createMessage(obj: Any, properties: MessageProperties): Message {
        println("CONVERTER: converting message to proto")
        obj as GeneratedMessageV3
        val bytes = obj.toByteArray()
        properties.contentLength = bytes.size.toLong()
        properties.contentType = ProtoMessage.MEDIA_TYPE
        properties.setHeader(ProtoMessage.TYPE_KEY, obj::class.java.simpleName)
        return Message(bytes, properties)
    }

    override fun fromMessage(message: Message): com.google.protobuf.Message {
        return when(message.messageProperties.headers[ProtoMessage.TYPE_KEY]) {
            NewPlayerRequest::class.java.simpleName -> NewPlayerRequest.parseFrom(message.body)
            BuyInRequest::class.java.simpleName -> BuyInRequest.parseFrom(message.body)
            BetRequest::class.java.simpleName -> BetRequest.parseFrom(message.body)
            else -> throw UnsupportedMessageException(message)
        }
    }

    object ProtoMessage {
        const val TYPE_KEY = "type"
        const val MEDIA_TYPE = MediaType.APPLICATION_OCTET_STREAM_VALUE
    }
}