package com.goosvandenbekerom.roulette.domain

import com.google.protobuf.Message

data class Request(val message: Message, val replyTo: String = "", val correlationKey: String = "")