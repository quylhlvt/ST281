package com.food.diydrink.foodmaker.ui.language

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import com.food.diydrink.foodmaker.R
import com.food.diydrink.foodmaker.core.base.BaseAdapter
import com.food.diydrink.foodmaker.core.extention.gone
import com.food.diydrink.foodmaker.core.extention.invisible
import com.food.diydrink.foodmaker.core.extention.loadImage
import com.food.diydrink.foodmaker.core.extention.onClick
import com.food.diydrink.foodmaker.core.extention.visible
import com.food.diydrink.foodmaker.data.model.language.LanguageModel
import com.food.diydrink.foodmaker.databinding.ItemLanguageBinding
import com.food.diydrink.foodmaker.core.extention.OuterStrokeShadownTextView

class LanguageAdapter (val context: Context) : BaseAdapter<LanguageModel, ItemLanguageBinding>(
    ItemLanguageBinding::inflate
) {
    var onItemClick: ((String) -> Unit) = {}
    override fun onBind(
        binding: ItemLanguageBinding, item: LanguageModel, position: Int
    ) {
        binding.apply {
            imvFlag.setImageResource(item.flag)
            btnRadio.setImageResource(if (item.activate) R.drawable.ic_select_lang else R.drawable.ic_un_select_lang)
            tvLang.text = item.name
            root.onClick { onItemClick.invoke(item.code) }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun submitItem(position: Int) {
        val oldSelected = items.indexOfFirst { it.activate }
        items.forEach { it.activate = false }
        items[position].activate = true
        // Chỉ update 2 item thay đổi, không redraw toàn bộ list
        if (oldSelected >= 0) notifyItemChanged(oldSelected)
        notifyItemChanged(position)
    }
}