package com.homework.ninedt.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.homework.ninedt.data.model.GameStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class DatabaseConverter {
    @TypeConverter
    fun getIntArray(listOfInt: String?): Array<Int> {
        return Gson().fromJson(
            listOfInt,
            object : TypeToken<Array<Int>>() {}.type
        )
    }

    @TypeConverter
    fun saveIntArray(listofInt: Array<Int>): String {
        return Gson().toJson(listofInt)
    }

    @TypeConverter
    fun toGameStatus(value: String) = enumValueOf<GameStatus>(value)

    @TypeConverter
    fun fromGameStatus(value: GameStatus) = value.name

    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return value.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}