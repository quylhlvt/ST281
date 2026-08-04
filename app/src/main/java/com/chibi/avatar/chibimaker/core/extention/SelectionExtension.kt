package com.chibi.avatar.chibimaker.core.extention

// SelectionExtension.kt
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.chibi.avatar.chibimaker.data.model.custom.SelectionIndex

fun List<*>.toCleanSelections(): ArrayList<SelectionIndex> {
    val gson = Gson()
    val json = gson.toJson(this)
    val type = object : TypeToken<ArrayList<SelectionIndex>>() {}.type
    return gson.fromJson(json, type)
}