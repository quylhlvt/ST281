package com.chibi.avatar.chibimaker.ui.main.add_character.adapter

import com.chibi.avatar.chibimaker.core.base.BaseAdapter
import com.chibi.avatar.chibimaker.core.extention.loadImage
import com.chibi.avatar.chibimaker.core.extention.onClick
import com.chibi.avatar.chibimaker.data.model.addcharacter.SelectedAddModel
import com.chibi.avatar.chibimaker.databinding.ItemSpeechBinding
import com.chibi.avatar.chibimaker.databinding.ItemStickerBinding
import com.chibi.avatar.chibimaker.databinding.ItemStickerBinding.inflate

class SpeechAdapter  : BaseAdapter<SelectedAddModel, ItemSpeechBinding>(ItemSpeechBinding::inflate) {
    var onItemClick: ((String) -> Unit) = {}
    var currentSelected = -1

    override fun onBind(binding: ItemSpeechBinding, item: SelectedAddModel, position: Int) {
        binding.apply {

            loadImage(root, item.path, imageView)
            root.onClick {
                selectItem(position)          // ← was missing entirely
                onItemClick.invoke(item.path)
            }
        }
    }

    fun selectItem(position: Int) {           // ← changed private → public
        if (position == currentSelected) return
        val old = currentSelected
        currentSelected = position
        if (old >= 0) notifyItemChanged(old)
        notifyItemChanged(position)
    }
}