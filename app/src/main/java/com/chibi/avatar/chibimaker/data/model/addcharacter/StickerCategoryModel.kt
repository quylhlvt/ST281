package com.chibi.avatar.chibimaker.data.model.addcharacter

data class StickerCategoryModel(
    val category: String,
    val quantity: Int,
    var isSelected: Boolean = false
) {
    fun imageUrls(): List<String> = (1..quantity).map { index ->
        "$STICKER_BASE_URL/$category/$index.png"
    }

    companion object {
        private const val STICKER_BASE_URL =
            "https://lvtglobal.site/public/app/ST246_ChibiAvatarDollMaker/bg/sticker"
    }
}
