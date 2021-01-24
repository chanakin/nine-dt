package com.homework.ninedt.data.model

data class GameException(override val message: String, override val cause: Throwable?) :
    Exception(message, cause)