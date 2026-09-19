package com.code.Quiz.core.database

import androidx.room.TypeConverter
import java.util.Date

/**
 * Type converters to allow Room to persist complex types.
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromStringList(value: List<String>?): String? = value?.joinToString(separator = ",")

    @TypeConverter
    fun toStringList(value: String?): List<String>? = value?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
}
