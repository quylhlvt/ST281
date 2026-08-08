package com.food.diydrink.foodmaker.core.extention

// SelectionExtension.kt
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.food.diydrink.foodmaker.data.model.custom.SelectionIndex

fun List<*>.toCleanSelections(): ArrayList<SelectionIndex> {
    val gson = Gson()
    val json = gson.toJson(this)
    val type = object : TypeToken<ArrayList<SelectionIndex>>() {}.type
    return gson.fromJson(json, type)
}