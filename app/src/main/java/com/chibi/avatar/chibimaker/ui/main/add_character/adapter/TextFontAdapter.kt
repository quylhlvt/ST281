package com.chibi.avatar.chibimaker.ui.main.add_character.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import com.chibi.avatar.chibimaker.R
import com.chibi.avatar.chibimaker.core.base.BaseAdapter
import com.chibi.avatar.chibimaker.core.extention.dp
import com.chibi.avatar.chibimaker.core.extention.gone
import com.chibi.avatar.chibimaker.core.extention.onClick
import com.chibi.avatar.chibimaker.core.extention.setFont
import com.chibi.avatar.chibimaker.core.extention.visible
import com.chibi.avatar.chibimaker.data.model.addcharacter.SelectedAddModel
import com.chibi.avatar.chibimaker.databinding.ItemFontBinding


class TextFontAdapter(val context: Context) : BaseAdapter<SelectedAddModel, ItemFontBinding>(ItemFontBinding::inflate) {
    var onTextFontClick: ((Int, Int) -> Unit) = { _, _ -> }
    private var currentSelected = 0

    override fun onBind(binding: ItemFontBinding, item: SelectedAddModel, position: Int) {
        binding.apply {
            val ctx= binding.root.context
            if (item.isSelected) {
                frameShadown.visible()
                frame.apply {
                    setCardBackgroundColor(ContextCompat.getColor(context, R.color.app_color3))
                    strokeColor = ContextCompat.getColor(context, R.color.app_color2)
                    strokeWidth = (4).dp(ctx)
                }
            } else {
                frameShadown.gone()
                frame.apply {
                    setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
               strokeColor = ContextCompat.getColor(context, R.color.app_color7)
                  strokeWidth = (1.4).dp(ctx)
                }
            }


            tvFont.setFont(item.color)

            root.onClick { onTextFontClick.invoke(item.color, position) }
        }
    }

    fun submitItem(position: Int, list: ArrayList<SelectedAddModel>) {
        if (position != currentSelected) {
            items.clear()
            items.addAll(list)

            notifyItemChanged(currentSelected)
            notifyItemChanged(position)

            currentSelected = position
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitListReset(list: ArrayList<SelectedAddModel>){
        items.clear()
        items.addAll(list)
        currentSelected = 0
        notifyDataSetChanged()
    }
}