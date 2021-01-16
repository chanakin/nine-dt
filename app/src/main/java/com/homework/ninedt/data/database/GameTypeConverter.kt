package com.homework.ninedt.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken

class GameTypeConverter {
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
}