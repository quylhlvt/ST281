package com.food.diydrink.foodmaker.ui.main.add_character.adapter

import androidx.core.content.ContextCompat
import com.food.diydrink.foodmaker.R
import com.food.diydrink.foodmaker.core.base.BaseAdapter
import com.food.diydrink.foodmaker.core.extention.onClick
import com.food.diydrink.foodmaker.data.model.addcharacter.StickerCategoryModel
import com.food.diydrink.foodmaker.databinding.ItemTittleBackgroundImageBinding

class StickerCategoryAdapter :
    BaseAdapter<StickerCategoryModel, ItemTittleBackgroundImageBinding>(
        ItemTittleBackgroundImageBinding::inflate
    ) {

    var onCategoryClick: ((StickerCategoryModel, Int) -> Unit) = { _, _ -> }

    override fun onBind(
        binding: ItemTittleBackgroundImageBinding,
        item: StickerCategoryModel,
        position: Int
    ) {

        binding.apply {
            txtTittle.text = item.category
            val context = binding.root.context
            if (item.isSelected) {
                frameTittle.background = ContextCompat.getDrawable(context, R.drawable.img_bg_tittle_selected)
                txtTittle.setTextColor(ContextCompat.getColor(context, R.color.app_color))
            } else {
                frameTittle.background = ContextCompat.getDrawable(context, R.drawable.img_bg_tittle_unselected)
                txtTittle.setTextColor(ContextCompat.getColor(context, R.color.white))
            }

        }
        binding.root.onClick { onCategoryClick(item, position) }
    }
}
