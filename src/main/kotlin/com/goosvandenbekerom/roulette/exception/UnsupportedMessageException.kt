package com.goosvandenbekerom.roulette.exception

class UnsupportedMessageException(message: Any) : Exception("Received unsupported message type: ${message::class.java}")