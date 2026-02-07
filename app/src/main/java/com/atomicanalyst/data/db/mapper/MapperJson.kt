package com.atomicanalyst.data.db.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private val gson = Gson()
private val stringListType = object : TypeToken<List<String>>() {}.type

fun encodeStringList(values: List<String>): String = gson.toJson(values, stringListType)

fun decodeStringList(value: String): List<String> {
    if (value.isBlank()) return emptyList()
    return runCatching {
        gson.fromJson<List<String>>(value, stringListType) ?: emptyList()
    }.getOrElse { emptyList() }
}
