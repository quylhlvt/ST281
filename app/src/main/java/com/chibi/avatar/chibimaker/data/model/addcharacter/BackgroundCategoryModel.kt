package com.chibi.avatar.chibimaker.data.model.addcharacter

data class BackgroundCategoryModel(
    val category: String,
    val quantity: Int,
    var isSelected: Boolean = false
) {
    fun imageUrls(): List<String> = (1..quantity).map { index ->
        "$BACKGROUND_BASE_URL/$category/$index.png"
    }

    companion object {
        private const val BACKGROUND_BASE_URL =
            "https://lvtglobal.site/public/app/ST246_ChibiAvatarDollMaker/bg/background"
    }
}
