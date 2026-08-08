package com.food.diydrink.foodmaker.data.model.custom

data class QuickMixItem(    val templateIndex: Int,
                            val template: CustomModel,
                            val selections: ArrayList<SelectionIndex>,
                            val resolvedPaths: List<String?>

)
