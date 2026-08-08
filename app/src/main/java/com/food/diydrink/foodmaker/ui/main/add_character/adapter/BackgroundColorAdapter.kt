package com.food.diydrink.foodmaker.ui.main.add_character.adapter

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.food.diydrink.foodmaker.R
import com.food.diydrink.foodmaker.core.base.BaseAdapter
import com.food.diydrink.foodmaker.core.extention.dp
import com.food.diydrink.foodmaker.core.extention.gone
import com.food.diydrink.foodmaker.core.extention.loadFromAsset
import com.food.diydrink.foodmaker.core.extention.loadImage
import com.food.diydrink.foodmaker.core.extention.onClick
import com.food.diydrink.foodmaker.core.extention.visible
import com.food.diydrink.foodmaker.data.model.addcharacter.SelectedAddModel
import com.food.diydrink.foodmaker.databinding.ItemBackgroundColorBinding


class BackgroundColorAdapter : BaseAdapter<SelectedAddModel, ItemBackgroundColorBinding>(
    ItemBackgroundColorBinding::inflate
) {
    var onChooseColorClick: (() -> Unit) = {}
    var onBackgroundColorClick: ((Int, Int) -> Unit) = { _, _ -> }
    var currentSelected = -1

    override fun onBind(binding: ItemBackgroundColorBinding, item: SelectedAddModel, position: Int) {
        val context = binding.root.context

        binding.apply {
            if (currentSelected == position) {
                materiaForcus.visible()
            } else {
                materiaForcus.gone()
            }
            if (position == 0) {
                imvAddColor.visible()
                imvColor.gone()
                root.onClick { onChooseColorClick()}
            } else {
                imvAddColor.gone()
                imvColor.visible()
                    imvColor.setBackgroundColor(item.color)
                    root.onClick { onBackgroundColorClick(item.color, position) }
            }

        }
    }

    fun selectItem(position: Int) {
        if (position == currentSelected) return
        val old = currentSelected
        currentSelected = position
        if (old >= 0) notifyItemChanged(old)
        if (position >= 0) notifyItemChanged(position)
    }

    fun clearSelection() {
        if (currentSelected < 0) return
        val old = currentSelected
        currentSelected = -1
        notifyItemChanged(old)
    }
}