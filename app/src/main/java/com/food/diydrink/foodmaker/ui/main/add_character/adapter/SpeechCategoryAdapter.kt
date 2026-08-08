package com.food.diydrink.foodmaker.ui.main.add_character.adapter

import androidx.core.content.ContextCompat
import com.food.diydrink.foodmaker.R
import com.food.diydrink.foodmaker.core.base.BaseAdapter
import com.food.diydrink.foodmaker.core.extention.onClick
import com.food.diydrink.foodmaker.data.model.addcharacter.SpeechCategoryModel
import com.food.diydrink.foodmaker.databinding.ItemTittleBackgroundImageBinding

class SpeechCategoryAdapter :
    BaseAdapter<SpeechCategoryModel, ItemTittleBackgroundImageBinding>(ItemTittleBackgroundImageBinding::inflate) {
    var onCategoryClick: ((SpeechCategoryModel, Int) -> Unit) = { _, _ -> }

    override fun onBind(binding: ItemTittleBackgroundImageBinding, item: SpeechCategoryModel, position: Int) {
        val context = binding.root.context
        binding.txtTittle.text = context.getString(R.string.bubbles)+" "+item.category
        binding.frameTittle.background = ContextCompat.getDrawable(
            context,
            if (item.isSelected) R.drawable.img_bg_tittle_selected
            else R.drawable.img_bg_tittle_unselected
        )
        binding.txtTittle.setTextColor(
            ContextCompat.getColor(context, if (item.isSelected) R.color.app_color else R.color.white)
        )
        binding.root.onClick { onCategoryClick(item, position) }
    }
}
