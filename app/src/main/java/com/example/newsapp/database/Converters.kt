package com.example.newsapp.database

import androidx.room.TypeConverter
import com.example.newsapp.model.Articles
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Class responsible for converting custom types to and from JSON strings
class Converters {

    // Convert JSON string to ArrayList of Articles objects
    @TypeConverter
    fun toArticlesList(json: String?): ArrayList<Articles?>? {
        if (json == null) return null
        // Create a TypeToken for ArrayList of Articles objects
        val typeToken = object : TypeToken<ArrayList<Articles?>?>() {}.type
        // Deserialize JSON string into ArrayList of Articles objects using Gson
        return Gson().fromJson(json, typeToken)
    }

    // Convert ArrayList of Articles objects to JSON string
    @TypeConverter
    fun fromArticlesList(list: ArrayList<Articles?>?): String? {
        return list?.let { Gson().toJson(it) }
    }
}