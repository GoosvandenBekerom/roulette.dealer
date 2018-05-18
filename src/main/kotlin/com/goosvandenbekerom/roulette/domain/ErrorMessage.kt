package com.goosvandenbekerom.roulette.domain

data class ErrorMessage(val message: String, val context: String, val playerName: String? = null)