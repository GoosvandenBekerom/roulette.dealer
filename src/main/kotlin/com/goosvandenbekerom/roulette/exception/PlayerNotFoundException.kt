package com.goosvandenbekerom.roulette.exception

class PlayerNotFoundException(id: Long) : Exception("Player with id $id was not found")