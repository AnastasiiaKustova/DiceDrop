package com.example.dicedrop.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T> Gson.fromJsonOrDefault(json: String?, default: T): T {
    return if (json.isNullOrEmpty()) default
    else runCatching {
        fromJson<T>(json, object : TypeToken<T>() {}.type)
    }.getOrElse { default }
}
