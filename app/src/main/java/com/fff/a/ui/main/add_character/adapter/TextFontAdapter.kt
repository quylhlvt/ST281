package com.fff.a.ui.main.add_character.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import com.fff.a.core.base.BaseAdapter
import com.fff.a.core.extention.onClick
import com.fff.a.core.extention.setFont
import com.fff.a.data.model.addcharacter.SelectedAddModel
import com.fff.a.R
import com.fff.a.core.extention.dp
import com.fff.a.core.extention.gone
import com.fff.a.core.extention.visible
import com.fff.a.databinding.ItemFontBinding


class TextFontAdapter(val context: Context) : BaseAdapter<SelectedAddModel, ItemFontBinding>(ItemFontBinding::inflate) {
    var onTextFontClick: ((Int, Int) -> Unit) = { _, _ -> }
    private var currentSelected = 0

    override fun onBind(binding: ItemFontBinding, item: SelectedAddModel, position: Int) {
        binding.apply {
            if (item.isSelected) {
                frame.apply {
                    strokeColor = ContextCompat.getColor(context, R.color.app_color2)
                }
            } else {
                frame.apply {
               strokeColor = ContextCompat.getColor(context, R.color.white)
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
        currentSelected = list.indexOfFirst { it.isSelected }.takeIf { it >= 0 } ?: 0
        notifyDataSetChanged()
    }
}
