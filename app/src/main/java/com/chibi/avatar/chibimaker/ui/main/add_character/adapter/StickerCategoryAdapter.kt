package com.chibi.avatar.chibimaker.ui.main.add_character.adapter

import androidx.core.content.ContextCompat
import com.chibi.avatar.chibimaker.R
import com.chibi.avatar.chibimaker.core.base.BaseAdapter
import com.chibi.avatar.chibimaker.core.extention.onClick
import com.chibi.avatar.chibimaker.data.model.addcharacter.StickerCategoryModel
import com.chibi.avatar.chibimaker.databinding.ItemTittleBackgroundImageBinding

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
                txtTittle.setTextColor(ContextCompat.getColor(context, R.color.white))
                txtTittle.setOuterStrokeColor(
                    ContextCompat.getColor(context, R.color.app_color2)
                )
                txtTittle.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.app_color8
                    )
                )
            } else {
                txtTittle.setTextColor(ContextCompat.getColor(context, R.color.gray2))
                txtTittle.setOuterStrokeColor(
                    ContextCompat.getColor(context, R.color.gray2)
                )
                txtTittle.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
            }

        }
        binding.root.onClick { onCategoryClick(item, position) }
    }
}
