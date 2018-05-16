package com.goosvandenbekerom.roulette.exception

class GameNotFoundException(id: Long) : Exception("Game with id $id was not found")