package com.chibi.avatar.chibimaker.ui.language

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import com.chibi.avatar.chibimaker.R
import com.chibi.avatar.chibimaker.core.base.BaseAdapter
import com.chibi.avatar.chibimaker.core.extention.gone
import com.chibi.avatar.chibimaker.core.extention.invisible
import com.chibi.avatar.chibimaker.core.extention.loadImage
import com.chibi.avatar.chibimaker.core.extention.onClick
import com.chibi.avatar.chibimaker.core.extention.visible
import com.chibi.avatar.chibimaker.data.model.language.LanguageModel
import com.chibi.avatar.chibimaker.databinding.ItemLanguageBinding
import com.chibi.avatar.chibimaker.core.extention.OuterStrokeShadownTextView

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
            imgLangFor.setImageResource(if (item.activate) R.drawable.frame_select_language else R.drawable.frame_unselect_language)
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