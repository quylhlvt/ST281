package com.food.diydrink.foodmaker.ui.main.add_character

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModel
import com.food.diydrink.foodmaker.core.custom.Draw
import com.food.diydrink.foodmaker.core.custom.DrawableDraw
import com.food.diydrink.foodmaker.data.model.addcharacter.SelectedAddModel
import com.food.diydrink.foodmaker.data.model.addcharacter.BackgroundCategoryModel
import com.food.diydrink.foodmaker.data.model.addcharacter.StickerCategoryModel
import com.food.diydrink.foodmaker.data.model.addcharacter.SpeechCategoryModel
import com.food.diydrink.foodmaker.utils.DataLocal
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddCharacterViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    // ========== Init guard ==========
    var isInitialized = false
    var isRestoringDraws = false

    // ========== Adapter Lists ==========
    var backgroundImageList: ArrayList<SelectedAddModel> = arrayListOf()
    var backgroundCategoryList: ArrayList<BackgroundCategoryModel> = arrayListOf()
    var stickerCategoryList: ArrayList<StickerCategoryModel> = arrayListOf()
    var speechCategoryList: ArrayList<SpeechCategoryModel> = arrayListOf()
    var backgroundColorList: ArrayList<SelectedAddModel> = arrayListOf()
    var stickerList: ArrayList<SelectedAddModel> = arrayListOf()
    var speechList: ArrayList<SelectedAddModel> = arrayListOf()
    var textFontList: ArrayList<SelectedAddModel> = arrayListOf()
    var textColorList: ArrayList<SelectedAddModel> = arrayListOf()

    // ========== Navigation ==========
    // -1 = chưa set, Fragment bỏ qua
    private val _typeNavigation = MutableStateFlow(-1)
    val typeNavigation: StateFlow<Int> = _typeNavigation.asStateFlow()

    private val _typeBackground = MutableStateFlow(-1)
    val typeBackground: StateFlow<Int> = _typeBackground.asStateFlow()

    // ========== Background ==========
    private val _backgroundImagePath = MutableStateFlow<String?>(null)
    val backgroundImagePath: StateFlow<String?> = _backgroundImagePath.asStateFlow()
    var selectedBackgroundImagePath: String? = null
    var selectedBackgroundImagePosition: Int = -1
    private val backgroundSelectionByCategory = mutableMapOf<String, Int>()
    private val backgroundPathByCategory = mutableMapOf<String, String?>()

    var savedBackgroundColor: Int? = null

    // ========== Tab state ==========
    // Chỉ dùng để biết tab nào đang active — KHÔNG dùng để control layout
    var isTextTabActive: Boolean = false
    var isSpeechDialogOpen: Boolean = false

    // ========== Draw state ==========
    var currentDraw: Draw? = null
    var drawViewList: ArrayList<DrawableDraw> = arrayListOf()

    // ========== Misc ==========
    var pathDefault = ""

    // ========== Navigation setters ==========

    fun setTypeNavigation(type: Int) {
        if (_typeNavigation.value == type) _typeNavigation.value = -1
        _typeNavigation.value = type
    }

    fun setTypeBackground(type: Int) {
        if (_typeBackground.value == type) _typeBackground.value = -1
        _typeBackground.value = type
    }

    fun setBackgroundImage(path: String?) {
        _backgroundImagePath.value = path
    }

    // ========== Data loading ==========

    fun loadDataFromMainViewModel(
        backgrounds: List<String>,
        stickers: List<String>,
        speeches: List<String>
    ) {
        backgroundImageList.clear()
        backgroundImageList.add(SelectedAddModel(path = "")) // None
        backgroundImageList.add(SelectedAddModel(path = "")) // Pick from gallery
        backgroundImageList.addAll(backgrounds.map { SelectedAddModel(path = it) })

        // Khi category đã có dữ liệu, luôn hiển thị danh sách của title
        // đang chọn thay vì để list backgrounds phẳng ghi đè lên nó.
        backgroundCategoryList.indexOfFirst { it.isSelected }
            .takeIf { it >= 0 }
            ?.let(::selectBackgroundCategory)

        backgroundColorList.clear()
        backgroundColorList.add(SelectedAddModel()) // None
        backgroundColorList.add(SelectedAddModel()) // Choose custom color
        backgroundColorList.addAll(DataLocal.getBackgroundColorDefault(context))

        stickerList.clear()
        stickerList.addAll(stickers.map { SelectedAddModel(path = it) })
        stickerCategoryList.indexOfFirst { it.isSelected }
            .takeIf { it >= 0 }
            ?.let(::selectStickerCategory)

        speechList.clear()
        speechList.addAll(speeches.map { SelectedAddModel(path = it) })
        speechCategoryList.indexOfFirst { it.isSelected }
            .takeIf { it >= 0 }
            ?.let(::selectSpeechCategory)

        textFontList.clear()
        textFontList.addAll(DataLocal.getTextFontDefault())
        textFontList.firstOrNull()?.isSelected = true

        textColorList.clear()
        textColorList.addAll(DataLocal.getTextColorDefault(context))
        textColorList.getOrNull(1)?.isSelected = true
    }

    fun setBackgroundCategories(categories: List<BackgroundCategoryModel>) {
        val selectedCategory = backgroundCategoryList
            .firstOrNull { it.isSelected }
            ?.category

        backgroundCategoryList = categories.mapIndexed { index, category ->
            if (selectedCategory == null) {
                category.copy(isSelected = index == 0)
            } else {
                category.copy(isSelected = category.category == selectedCategory)
            }
        }.toCollection(ArrayList())

        val selectedPosition = backgroundCategoryList.indexOfFirst { it.isSelected }
        if (selectedPosition >= 0) {
            val category = backgroundCategoryList[selectedPosition].category
            if (backgroundSelectionByCategory[category] == null) {
                backgroundSelectionByCategory[category] = NONE_BACKGROUND_POSITION
                backgroundPathByCategory[category] = null
            }
            selectBackgroundCategory(selectedPosition)
        }
    }

    fun selectBackgroundCategory(position: Int): List<String> {
        backgroundCategoryList.forEachIndexed { index, item ->
            item.isSelected = index == position
        }
        val urls = backgroundCategoryList.getOrNull(position)?.imageUrls().orEmpty()
        val category = backgroundCategoryList.getOrNull(position)?.category.orEmpty()
        // Add-image and None are global choices: keep their focus when the
        // user switches background categories. Remote images remain per-tag.
        val globalSpecialSelection = selectedBackgroundImagePosition
            .takeIf { it == ADD_BACKGROUND_POSITION || it == NONE_BACKGROUND_POSITION }
        selectedBackgroundImagePosition =
            backgroundSelectionByCategory[category] ?: globalSpecialSelection ?: -1
        selectedBackgroundImagePath = backgroundPathByCategory[category]
        backgroundImageList = arrayListOf(
            SelectedAddModel(
                path = "",
                isSelected = selectedBackgroundImagePosition == NONE_BACKGROUND_POSITION
            ),
            SelectedAddModel(
                path = "",
                isSelected = selectedBackgroundImagePosition == ADD_BACKGROUND_POSITION
            )
        ).apply {
            addAll(urls.map {
                SelectedAddModel(path = it, isSelected = it == selectedBackgroundImagePath)
            })
        }
        selectedBackgroundImagePosition = backgroundImageList.indexOfFirst { it.isSelected }
        return urls
    }

    fun setStickerCategories(categories: List<StickerCategoryModel>) {
        val selected = stickerCategoryList.firstOrNull { it.isSelected }?.category
        stickerCategoryList = categories.mapIndexed { index, category ->
            category.copy(isSelected = selected?.let { it == category.category } ?: (index == 0))
        }.toCollection(ArrayList())

        stickerCategoryList.indexOfFirst { it.isSelected }
            .takeIf { it >= 0 }
            ?.let(::selectStickerCategory)
    }

    fun selectStickerCategory(position: Int) {
        stickerCategoryList.forEachIndexed { index, item ->
            item.isSelected = index == position
        }
        stickerList = stickerCategoryList.getOrNull(position)
            ?.imageUrls()
            .orEmpty()
            .map { SelectedAddModel(path = it) }
            .toCollection(ArrayList())
    }

    fun setSpeechCategories(categories: List<SpeechCategoryModel>) {
        val selected = speechCategoryList.firstOrNull { it.isSelected }?.category
        speechCategoryList = categories.mapIndexed { index, category ->
            category.copy(isSelected = selected?.let { it == category.category } ?: (index == 0))
        }.toCollection(ArrayList())
    }

    fun selectSpeechCategory(position: Int) {
        speechCategoryList.forEachIndexed { index, item -> item.isSelected = index == position }
        speechList = speechCategoryList.getOrNull(position)?.imageUrls().orEmpty()
            .map { SelectedAddModel(path = it) }.toCollection(ArrayList())
    }

    // ========== Selection helpers ==========

    fun updateBackgroundImageSelected(position: Int) {
        selectedBackgroundImagePosition = position
        val category = backgroundCategoryList.firstOrNull { it.isSelected }?.category
        if (position == ADD_BACKGROUND_POSITION || position == NONE_BACKGROUND_POSITION) {
            // These two entries are not tied to a remote category.
            backgroundCategoryList.forEach {
                backgroundSelectionByCategory[it.category] = position
                backgroundPathByCategory[it.category] = null
            }
        } else if (category != null) {
            // Selecting an image in a new tag clears every previous focus;
            // only the newly selected image remains focused.
            backgroundSelectionByCategory.clear()
            backgroundPathByCategory.clear()
            backgroundSelectionByCategory[category] = position
            backgroundPathByCategory[category] = selectedBackgroundImagePath
        }
        backgroundColorList.forEach { it.isSelected = false }
        backgroundImageList.forEachIndexed { index, model ->
            model.isSelected = index == position
        }
    }

    fun updateBackgroundColorSelected(position: Int) {
        selectedBackgroundImagePosition = -1
        selectedBackgroundImagePath = null
        backgroundSelectionByCategory.clear()
        backgroundPathByCategory.clear()
        backgroundImageList.forEach { it.isSelected = false }
        backgroundColorList.forEachIndexed { index, model ->
            model.isSelected = index == position
        }
    }

    private companion object {
        const val NONE_BACKGROUND_POSITION = 0
        const val ADD_BACKGROUND_POSITION = 1
    }

    fun updateTextFontSelected(position: Int) {
        textFontList = textFontList
            .map { it.copy(isSelected = false) }
            .toCollection(ArrayList())
        textFontList.forEachIndexed { index, model ->
            model.isSelected = index == position
        }
    }

    fun updateTextColorSelected(position: Int) {
        textColorList = textColorList
            .map { it.copy(isSelected = false) }
            .toCollection(ArrayList())
        textColorList.forEachIndexed { index, model ->
            model.isSelected = index == position
        }
    }

    // ========== Draw helpers ==========

    fun updateCurrentCurrentDraw(draw: Draw) {
        currentDraw = draw
    }

    fun addDrawView(draw: Draw) {
        if (draw is DrawableDraw) {
            drawViewList.add(draw)
        }
    }

    fun deleteDrawView(draw: Draw) {
        drawViewList.removeIf { it == draw }
    }

    fun resetDraw() {
        drawViewList.clear()
        currentDraw = null
    }

    fun updatePathDefault(path: String) {
        pathDefault = path
    }

    // ========== Drawable / Emoji ==========

    fun loadDrawableEmoji(
        bitmap: Bitmap,
        isCharacter: Boolean = false,
        isText: Boolean = false
    ): DrawableDraw {
        val drawable = bitmap.toDrawable(context.resources)
        val timestamp = SimpleDateFormat("dd_MM_yyyy_hh_mm_ss").format(Date())
        val drawableEmoji = DrawableDraw(drawable, "$timestamp.png")
        drawableEmoji.isCharacter = isCharacter
        drawableEmoji.isText = isText
        return drawableEmoji
    }

    // ========== Cleanup ==========

    fun clearAllData() {
        backgroundImageList.clear()
        backgroundCategoryList.clear()
        stickerCategoryList.clear()
        speechCategoryList.clear()
        backgroundColorList.clear()
        stickerList.clear()
        speechList.clear()
        textFontList.clear()
        textColorList.clear()
        drawViewList.clear()
        currentDraw = null
        pathDefault = ""
    }
}
