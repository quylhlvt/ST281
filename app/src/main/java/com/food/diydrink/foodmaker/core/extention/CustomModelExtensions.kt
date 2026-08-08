package com.food.diydrink.foodmaker.core.extention

import com.google.gson.Gson
import com.food.diydrink.foodmaker.data.model.custom.BodyPartModel
import com.food.diydrink.foodmaker.data.model.custom.ColorModel
import com.food.diydrink.foodmaker.data.model.custom.CustomModel

private val extensionGson = Gson()

fun CustomModel.withCleanListPath(): CustomModel {
    val cleanListPath = listPath.map { bp ->
        val bpJson = extensionGson.toJson(bp)
        val bodyPart = extensionGson.fromJson(bpJson, BodyPartModel::class.java)
        val cleanColors = bodyPart.listPath.map { color ->
            val colorJson = extensionGson.toJson(color)
            extensionGson.fromJson(colorJson, ColorModel::class.java)
        }
        bodyPart.copy(listPath = ArrayList(cleanColors))
    }
    return this.copy(listPath = ArrayList(cleanListPath))
}